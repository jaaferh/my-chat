package com.mindlinksoft.recruitment.mychat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.mindlinksoft.recruitment.mychat.Filters.*;

/**
 * Represents a conversation exporter that can read a conversation and write it
 * out in JSON.
 */
public class ConversationExporter {

	/**
	 * The application entry point.
	 * 
	 * @param args The command line arguments.
	 * @throws IOException Thrown when there is an error in input or output.
	 */
	public static void main(String[] args) throws IOException {
		ConversationExporter exporter = new ConversationExporter();
		ConversationExporterConfiguration config = new CommandLineArgumentParser().parseArguments(args);

		exporter.exportConversation(config.inputFilePath, config.outputFilePath, config.option);
	}

	/**
	 * Exports the conversation at {@code inputFilePath} as JSON to
	 * {@code outputFilePath}.
	 * 
	 * @param inputFilePath  The input file path.
	 * @param outputFilePath The output file path.
	 * @param option         The command line arguments given at launch.
	 * @throws IOException Thrown when there is an error in input or output.
	 */
	public void exportConversation(String input, String output, String[] option) throws IOException {
		Conversation conversation = this.readConversation(input);

		// Check all arguments for any options (like -obf), and execute each option in
		// the arguments
		for (int i = 0; i < option.length; i++) {

			if (option[i].contains("-")) {
				String argument = "";

				switch (option[i]) {
				case "-user":
					argument = option[i+1];
					Filter uf = new UserFilter(argument);
					conversation = uf.filterMessages(conversation);
					System.out.println("Messages not from '" + option[1] + "' filtered out.");
					i++;
					break;
				case "-key":
					argument = option[i+1];
					Filter kw = new KeywordFilter(argument);
					conversation = kw.filterMessages(conversation);
					System.out.println("Messages not including '" + option[1] + "' filtered out.");
					i++;
					break;
				case "-hidewords":
					argument = option[i+1];
					Filter bl = new BlacklistFilter(argument);
					conversation = bl.filterMessages(conversation);
					System.out.println("Blacklisted words filtered.");
					i++;
					break;
				case "-hidenum":
					Filter nf = new NumberFilter();
					conversation = nf.filterMessages(conversation);
					System.out.println("Card and phone numbers redacted.");
					break;
				case "-obf":
					Filter obf = new ObfuscateIDFilter();
					conversation = obf.filterMessages(conversation);
					System.out.println("Sender Ids obfuscated.");
					break;
				case "-report":
					conversation = ActivityReport.addReport(conversation);
					System.out.println("Activity report created");
				}

			} else {
				i++;
			}

		}
		this.writeConversation(conversation, output);
		System.out.println("Conversation exported from '" + input + "' to '" + output + "'");
	}

	/**
	 * Helper method to write the given {@code conversation} as JSON to the given
	 * {@code outputFilePath}.
	 * 
	 * @param conversation   The conversation to write.
	 * @param outputFilePath The file path where the conversation should be written.
	 * @throws IOException Thrown when there is an error in input or output.
	 */
	public void writeConversation(Conversation conversation, String output) throws IOException {
		// TODO: Do we need both to be resources, or will buffered writer close the
		// stream?
		try (OutputStream os = new FileOutputStream(output, true);
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os))) {

			String jsonConvo = InstantSerializer.createJsonSerialized(conversation);
			bw.write(jsonConvo);
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException("The file '" + output + "' was not found.");
		} catch (IOException e) {
			throw new IOException("BufferedWriter failed to write file");
		}
	}

	/**
	 * Represents a helper to read a conversation from the given
	 * {@code inputFilePath}.
	 * 
	 * @param inputFilePath The path to the input file.
	 * @return The {@link Conversation} representing by the input file.
	 * @throws IOException Thrown when there is an error in input or output.
	 */
	public Conversation readConversation(String input) throws IOException {
		try (InputStream is = new FileInputStream(input);
				BufferedReader r = new BufferedReader(new InputStreamReader(is))) {

			List<Message> messages = new ArrayList<Message>();

			String conversationName = r.readLine();
			String line;

			while ((line = r.readLine()) != null) {
				String[] split = line.split(" ", 3); // Splits each string to 3 substrings

				messages.add(new Message(Instant.ofEpochSecond(Long.parseUnsignedLong(split[0])), split[1], split[2]));
			}
			return new Conversation(conversationName, messages);
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException("The file '" + input + "' was not found.");
		} catch (IOException e) {
			throw new IOException("BufferedReader failed to read file");
		}
	}

}
