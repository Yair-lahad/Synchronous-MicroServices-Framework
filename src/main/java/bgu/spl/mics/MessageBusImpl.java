package bgu.spl.mics;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */

public class MessageBusImpl implements MessageBus {
	// links the micro to its queue
	private ConcurrentHashMap<MicroService,LinkedBlockingQueue<Message>> microsQs;
	// Links the micro to the Messages it is registered to
	private ConcurrentHashMap<MicroService, ConcurrentLinkedQueue<Class<? extends Message>>> microRegistrations;
	// links the type of Message to the micros that handles it
	private ConcurrentHashMap<Class<? extends Message>,ConcurrentLinkedQueue<MicroService>> messageMap;
	// links the event to its associated future
	private ConcurrentHashMap<Event,Future> futures;

	private static class SingletonHolder {
		private static MessageBusImpl instance = new MessageBusImpl();
	}

	private MessageBusImpl(){
		microsQs = new ConcurrentHashMap<>();
		microRegistrations = new ConcurrentHashMap<>();
		messageMap = new ConcurrentHashMap<>();
		futures = new ConcurrentHashMap<>();
	}

	public static MessageBusImpl getInstance() {
		return SingletonHolder.instance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		subscribeMessage(type,m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		subscribeMessage(type,m);
    }

    // private method which subscribe microService to a given type of message
    private void subscribeMessage(Class<? extends Message> type, MicroService m) {
		messageMap.putIfAbsent(type,new ConcurrentLinkedQueue<MicroService>());
		// insert the microService to the correct Message type queue
		synchronized (type) { // in case 2 threads access the same MessageQ
			ConcurrentLinkedQueue<MicroService> tmpQ1 = messageMap.get(type);
			tmpQ1.add(m);
		}
		// updates which messages the current MicroService is subscribed to
		microRegistrations.putIfAbsent(m,new ConcurrentLinkedQueue<Class<? extends Message>>());   // should we sync ?
		ConcurrentLinkedQueue<Class<? extends Message>> tmpQ2 = microRegistrations.get(m);
		tmpQ2.add(type);
	}

	@Override @SuppressWarnings("unchecked")
	public <T> void complete(Event<T> e, T result) {
	//	synchronized (futures.get(e)) {
			futures.get(e).resolve(result);
	//	}
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		ConcurrentLinkedQueue<MicroService> tmpQ = messageMap.get(b.getClass());
		for (MicroService m: tmpQ)
			synchronized (b.getClass()){
				microsQs.get(m).add(b);
			}
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		// builds the associated future
		Future fe = new Future<T>();
		futures.put(e,fe);
		// takes the queue of the micros subscribed to this event
		ConcurrentLinkedQueue<MicroService> tmpQ = messageMap.get(e.getClass());
		MicroService tmpMicro;
		if (tmpQ == null)
			return null;
		synchronized (e.getClass()){   // in case of few senders, lock the Micros Q of the specific event till the micro is back to the end of the q
			// which micro will get this event
			tmpMicro = tmpQ.poll();
			if (tmpMicro ==null) return null; // (for generic impl) : in case all micros unregistered ///
				// return the micro to this Event type queue - maintains round robin
				tmpQ.add(tmpMicro);
		}
		// inserts the event to the Micro's queue
		synchronized (tmpMicro){  // in case there are few send Events to the same micro
			microsQs.get(tmpMicro).add(e);
		}
		return fe;
	}

	@Override
	public void register(MicroService m) {
		LinkedBlockingQueue<Message> mQueue = new LinkedBlockingQueue<>();
		// register only once
		microsQs.putIfAbsent(m,mQueue);
	}

	@Override
	public void unregister(MicroService m) {
		// no need to sync since the same micro will not register and unregister at the same time
		microsQs.remove(m);
		ConcurrentLinkedQueue<Class<? extends Message>> tmp= microRegistrations.get(m);
		// remove the microService from each event it registered to
		for (Class<? extends Message> i : tmp)
			// in case 2 threads access to the same message type (relevant methods :subscribe, unregister etc.)
			synchronized (i){
				messageMap.get(i).remove(m);
			}
		// no need to sync since subscribe and unregister of the same micro will not occurs at the same time
		microRegistrations.remove(m);
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		// takes the first element in the queue, if empty waits until becomes available
			return microsQs.get(m).take();
	}
}
