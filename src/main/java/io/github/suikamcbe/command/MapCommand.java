package io.github.suikamcbe.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemFilledMap;
import cn.nukkit.permission.Permission;
import cn.nukkit.utils.TextFormat;
import io.github.suikamcbe.MapImagePlugin;

import java.io.File;
import java.util.Map;
import java.util.Objects;

public class MapCommand extends Command {
    public MapCommand(String name) {
        super(name);

        this.setPermission(Permission.DEFAULT_OP);

        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newEnum("type", new String[]{"url", "file"}),
                CommandParameter.newType("url_or_filename", CommandParamType.TEXT)
        });

        this.enableParamTree();
    }

    @Override
    public int execute(
            CommandSender sender,
            String commandLabel,
            Map.Entry<String, ParamList> result,
            CommandLogger log
    ) {
        if(!sender.isPlayer()) {
            log.addError("このコマンドはプレイヤーのみ実行できます。\n - This command can only be executed by players.");
            log.output();

            return 0;
        }

        Player player = sender.asPlayer();
        ParamList list = result.getValue();

        String type = list.getResult(0);
        String urlOrFileName = list.getResult(1);

        if(Objects.equals(type, "url") && !urlOrFileName.startsWith("http")) {
            log.addError("無効なURLです。URLはhttpまたはhttpsで始まる必要があります。\n - Invalid URL. URL must start with http or https.");
            log.output();

            return 0;
        }

        if(Objects.equals(type, "file")) {
            File file = new File(MapImagePlugin.getInstance().getDataFolder().toString(), "images/" + urlOrFileName);

            if(!file.exists()) {
                log.addError(TextFormat.GRAY + urlOrFileName + TextFormat.RED + "は存在しないファイルです。画像を正しくimagesフォルダ内に配置できていますか？");
                log.output();

                return 0;
            }
        }

        Item item = player.getInventory().getItemInHand();

        if(item instanceof ItemFilledMap map) {
            try {
                ItemFilledMap filledItem = MapImagePlugin.getInstance().setMapImage(map, urlOrFileName);

                player.getInventory().setItemInHand(filledItem);

                log.addSuccess("マップの画像を設定しました。\n - Set the image of the map.");
                log.output();
            } catch(Exception e) {
                log.addError("マップの画像を設定できませんでした。\n - Failed to set the image of the map.");
                log.output();

                MapImagePlugin.getInstance().getLogger().error("Failed to set the image of the map.", e);

                return 0;
            }
        } else {
            log.addError("手に持っているアイテムがマップではありません。\n - The item you are holding is not a map.");
            log.output();

            return 0;
        }

        return 1;
    }
}
