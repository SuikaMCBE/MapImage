package io.github.suikamcbe;

import cn.nukkit.Nukkit;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.item.ItemFilledMap;
import cn.nukkit.plugin.PluginBase;
import io.github.suikamcbe.command.MapCommand;
import lombok.Getter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URI;
import java.net.URL;

public class MapImagePlugin extends PluginBase {
    @Getter
    private static MapImagePlugin instance;

    @Override
    public void onEnable() {
        instance = this;

        this.getDataFolder().mkdirs();

        File imagesFolder = new File(this.getDataFolder() + "/images");

        imagesFolder.mkdirs();

        this.getServer().getCommandMap()
                .register("MapImage", new MapCommand("mapimage"));
    }

    public ItemFilledMap setMapImage(ItemFilledMap item, String urlOrFileName) throws Exception {
        if(urlOrFileName.startsWith("http")) {
            BufferedImage image = getBufferedImageFromURL(urlOrFileName);

            item.setImage(image);

            for(Player p : Server.getInstance().getOnlinePlayers().values()) {
                item.sendImage(p);
            }

            return item;
        } else {
            item.setImage(new File(this.getDataFolder() + "/images/" + urlOrFileName));

            for(Player p : Server.getInstance().getOnlinePlayers().values()) {
                item.sendImage(p);
            }

            return item;
        }
    }

    public BufferedImage getBufferedImageFromURL(String link) throws Exception {
        URL url = URI.create(link).toURL();

        return ImageIO.read(url);
    }
}