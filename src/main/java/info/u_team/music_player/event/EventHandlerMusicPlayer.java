package info.u_team.music_player.event;

import info.u_team.music_player.gui.GuiMusicPlayer;
import info.u_team.music_player.init.MusicPlayerKeys;
import net.minecraft.client.Minecraft;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class EventHandlerMusicPlayer {

	// Forge key board events are currently not implemented # 5481 So we are using
	// this to listen to keys. Its not that performant but its working at least for
	// now
	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (MusicPlayerKeys.open.isKeyDown()) {
			System.out.println("OPEN GUI");
			Minecraft.getInstance().displayGuiScreen(new GuiMusicPlayer());
		}
	}

}