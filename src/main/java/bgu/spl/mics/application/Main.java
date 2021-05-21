package bgu.spl.mics.application;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/** This is the Main class of the application. You should parse the input file, 
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */

public class Main {
	public static void main(String[] args) {
		try {
			Input inputJ = JsonInputReader.getInputFromJson(args[0]);
			// construct the passive objects
			Diary battleD = Diary.getInstance();
			Ewoks ewoks = Ewoks.getInstance();
			ewoks.initialize(inputJ.getEwoks());
			MessageBusImpl MB = MessageBusImpl.getInstance();
			// creating all MicroServices Threads
			int numberOfMicros = 4;
			latch = new CountDownLatch(numberOfMicros);
			int numOfThreads = 5;
			Thread[] threads = new Thread[numOfThreads];
			threads[0] = new Thread(new LeiaMicroservice(inputJ.getAttacks()));
			threads[1] = new Thread(new HanSoloMicroservice());
			threads[2] = new Thread(new C3POMicroservice());
			threads[3] = new Thread(new R2D2Microservice(inputJ.getR2D2()));
			threads[4] = new Thread(new LandoMicroservice(inputJ.getLando()));
			// running the program
			for (Thread thread : threads)
				thread.start();
			for (Thread thread : threads)
				thread.join();
			// Diary output to json
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			FileWriter writer = new FileWriter(args[1]);
			gson.toJson(battleD,writer);
			writer.flush();
			writer.close();

		} catch (IOException | InterruptedException e) {e.printStackTrace();}
	}
	// count down to indicate Leia that all other micros subscribed to their events
	public static CountDownLatch latch;

}
