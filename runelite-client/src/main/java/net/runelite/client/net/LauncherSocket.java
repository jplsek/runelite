package net.runelite.client.net;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.RuneLiteProperties;

@Singleton
@Slf4j
public class LauncherSocket
{
	private PrintWriter serverWriter;
	private Socket clientSocket;
	private int launcherPort;

	@Inject
	public LauncherSocket(RuneLiteProperties runeLiteProperties)
	{
		launcherPort = runeLiteProperties.getLauncherSocketPort();

		if (launcherPort == -1)
		{
			return;
		}

		try
		{
			clientSocket = new Socket("localhost", launcherPort);
			serverWriter = new PrintWriter(clientSocket.getOutputStream(), true);
		}
		catch (IOException e)
		{
			log.warn("Error starting launcher socket connection", e);
			this.launcherPort = -1;
		}
	}

	public void send(String message)
	{
		// handy for "debugging" without --debug for normal users (and without using the launcher)
		log.info(message);

		if (launcherPort == -1)
		{
			return;
		}

		serverWriter.println(message);
	}

	public void close() throws IOException
	{
		if (launcherPort == -1)
		{
			return;
		}

		send("\0");
		serverWriter.close();
		clientSocket.close();
	}
}
