package org.matsim.simwrapper;

import org.matsim.application.CommandRunner;
import org.matsim.application.MATSimAppCommand;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages and holds data needed for {@link org.matsim.simwrapper.SimWrapper},
 */
public final class Data {

	/**
	 * Maps context to command runners.
	 */
	private final Map<String, CommandRunner> runners = new HashMap<>();

	/**
	 * Resources that needs to be copied.
	 */
	private final Map<Path, URL> resources = new HashMap<>();

	/**
	 * The output directory.
	 */
	private Path path;
	private CommandRunner currentContext;

	public Data() {

		// path needed, but not available yet
		currentContext = runners.computeIfAbsent("", CommandRunner::new);
	}

	/**
	 * Set the default args that will be used for a specific command.
	 */
	public Data args(Class<? extends MATSimAppCommand> command, String... args) {
		currentContext.add(command, args);
		return this;
	}

	/**
	 * Set shp file for specific command, otherwise default shp will be used.
	 */
	public Data shp(Class<? extends MATSimAppCommand> command, String path) {
		currentContext.setShp(command, path);
		return this;
	}

	/**
	 * Reference to a file within the runs output directory.
	 *
	 * @param first name of the file or first part of the path
	 * @param path  don't use path separators, but multiple arguments
	 */
	public String output(String first, String... path) {
		return this.path.getParent().resolve(Path.of(first, path)).toString();
	}

	/**
	 * Uses a command to construct the required output.
	 *
	 * @param command the command to be executed
	 * @param file    name of the produced output file
	 */
	public String compute(Class<? extends MATSimAppCommand> command, String file, String... args) {
		currentContext.add(command, args);
		Path path = currentContext.getRequiredPath(command, file);

		// Relative path from the simulation output
		return this.path.getParent().relativize(path).toString();
	}

	public String subcommand(String command, String file) {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	/**
	 * Copies and references are file bundled in the classpath.
	 *
	 * @param name name of the resource
	 */
	public String resource(String name) {

		URL resource = this.getClass().getResource(name);

		if (resource == null)
			throw new IllegalArgumentException("Resource '" + name + "' not found!");

		Path res = Path.of(resource.getPath());

		Path baseDir;
		if (currentContext.getName().isBlank())
			baseDir = this.path.resolve("resources");
		else
			baseDir = this.path.resolve("resources-" + currentContext.getName());

		// Final path where resource should be copied
		Path resolved = baseDir.resolve(res.getFileName().toString());

		try {
			if (resources.containsKey(resolved) && !resources.get(resolved).toURI().equals(resource.toURI()))
				throw new IllegalArgumentException(String.format("Resource '%s' was already mapped to resource '%s'. ", name, resources.get(resolved)));

		} catch (URISyntaxException e) {
			throw new RuntimeException("Illegal URL", e);
		}

		return this.path.getParent().relativize(resolved).toString();
	}


	/**
	 * Switch to a different context, which can hold different arguments and shp options.
	 */
	void setCurrentContext(String name) {
		currentContext = runners.computeIfAbsent(name, CommandRunner::new);
		currentContext.setOutput(path);
	}

	void setPath(Path path) {
		this.path = path;
	}

	Map<String, CommandRunner> getRunners() {
		return runners;
	}

	Map<Path, URL> getResources() {
		return resources;
	}
}
