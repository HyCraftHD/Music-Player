package info.u_team.music_player.dependency;

import java.io.IOException;
import java.lang.reflect.*;
import java.net.*;
import java.nio.file.*;
import java.util.function.Consumer;

import org.apache.logging.log4j.*;

import info.u_team.music_player.MusicPlayerMod;
import info.u_team.music_player.dependency.classloader.DependencyClassLoader;
import info.u_team.music_player.dependency.url.UrlStreamHandlerMusicPlayer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;

public class DependencyManager {

	private static final Logger logger = LogManager.getLogger();
	private static final Marker load = MarkerManager.getMarker("Load");

	private static final DependencyClassLoader musicplayerclassloader = new DependencyClassLoader();

	public static void construct() {
		logger.info(load, "Load dependencies");

		final String devPath = System.getProperty("musicplayer.dev");
		if (devPath != null) {
			Paths.get(devPath, "musicplayer-lavaplayer/build/libs").forEach(DependencyManager::addToMusicPlayerDependencies);
			Paths.get(devPath, "musicplayer-lavaplayer/build/dependencies").forEach(DependencyManager::addToMusicPlayerDependencies);
		} else {
			setURLStreamHandlerFactory();
			getJarFilesInJar("dependencies/internal", path -> addToInternalDependencies(createInternalURL(path)));
			getJarFilesInJar("dependencies/musicplayer", path -> addToMusicPlayerDependencies(createInternalURL(path)));
		}

		logger.info(load, "Finished loading dependencies");
	}

	public static DependencyClassLoader getClassLoader() {
		return musicplayerclassloader;
	}

	private static void getJarFilesInJar(String folder, Consumer<Path> consumer) {
		final ModFile modfile = ModList.get().getModFileById(MusicPlayerMod.modid).getFile();
		try {
			Files.walk(modfile.findResource("/" + folder)).filter(file -> file.toString().endsWith(".jar")).forEach(consumer);
		} catch (IOException ex) {
			logger.error(load, "When searching for jar files in jar an exception occured.", ex);
		}
	}

	private static URL createInternalURL(Path path) {
		final String url = "musicplayer:" + path.toString().substring(1);
		logger.debug("Load url" + url);
		try {
			return new URL(url);
		} catch (MalformedURLException ex) {
			logger.error(load, "Could not create url from internal path", ex);
		}
		return null;
	}

	private static void addToMusicPlayerDependencies(URL url) {
		musicplayerclassloader.addURL(url);
	}

	private static void addToMusicPlayerDependencies(Path path) {
		musicplayerclassloader.addPath(path);
	}

	private static void addToInternalDependencies(URL url) {
		try {
			final URLClassLoader systemloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
			final Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
			method.setAccessible(true);
			method.invoke(systemloader, url);
		} catch (Exception ex) {
			logger.error(load, "Method addURL on system classloader could not be invoked", ex);
		}
	}

	// Really hackary because we need to use reflections to modify this..
	private static void setURLStreamHandlerFactory() {
		try {
			Field field = URL.class.getDeclaredField("factory");
			field.setAccessible(true);

			final URLStreamHandlerFactory factory = (URLStreamHandlerFactory) field.get(null);
			URLStreamHandlerFactory newFactory = protocol -> "musicplayer".equals(protocol) ? new UrlStreamHandlerMusicPlayer() : factory.createURLStreamHandler(protocol);
			field.set(null, newFactory);
		} catch (Exception ex) {
			logger.error(load, "Could not hack URL factory.", ex);
		}
	}
}
