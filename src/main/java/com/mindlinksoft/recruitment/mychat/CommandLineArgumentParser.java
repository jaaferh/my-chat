package com.mindlinksoft.recruitment.mychat;

/**
 * Represents a helper to parse command line arguments.
 */
public final class CommandLineArgumentParser {
    /**
     * Parses the given {@code arguments} into the exporter configuration.
     * @param arguments The command line arguments.
     * @return The exporter configuration representing the command line arguments.
     */
    public ConversationExporterConfiguration parseArguments(String[] arguments) {
    	String[] options = {arguments[2], arguments[3]};
        return new ConversationExporterConfiguration(arguments[0], arguments[1], options);
    }
}
