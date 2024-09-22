package org.alexdev.kepler.game.commands.registered;

import org.alexdev.kepler.Kepler;
import org.alexdev.kepler.game.commands.Command;
import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.fuserights.Fuseright;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.messages.outgoing.user.ALERT;
import org.alexdev.kepler.util.DateUtil;

public class UptimeCommand extends Command {
    private static final int UPTIME_COMMAND_INTERVAL_SECONDS = 5;
    private static long UPTIME_COMMAND_EXPIRY = 0L;

    private static int ACTIVE_PLAYERS = 0;
    private static int AUTHENTICATED_PLAYERS = 0;

    public UptimeCommand(){
        UPTIME_COMMAND_EXPIRY = DateUtil.getCurrentTimeSeconds();
    }

    @Override
    public void addPermissions() {
        this.permissions.add(Fuseright.DEFAULT);
    }

    @Override
    public void handleCommand(Entity entity, String message, String[] args) {
        if (entity.getType() != EntityType.PLAYER) {
            return;
        }

        Player player = (Player) entity;

        if (DateUtil.getCurrentTimeSeconds() > UPTIME_COMMAND_EXPIRY) {
            AUTHENTICATED_PLAYERS = PlayerManager.getInstance().getPlayers().size();
            ACTIVE_PLAYERS = PlayerManager.getInstance().getActivePlayers().size();

            Runtime runtime = Runtime.getRuntime();
            UPTIME_COMMAND_EXPIRY = DateUtil.getCurrentTimeSeconds() + UPTIME_COMMAND_INTERVAL_SECONDS;
        }

        long uptime = (DateUtil.getCurrentTimeSeconds() - Kepler.getStartupTime()) * 1000;
        long days = (uptime / (1000 * 60 * 60 * 24));
        long hours = (uptime - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (uptime - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (uptime - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60) - minutes * (1000 * 60)) / (1000);

        StringBuilder msg = new StringBuilder();
        msg.append("Oculus uptime is " + days + " day(s), " + hours + " hour(s), " + minutes + " minute(s) and " + seconds + " second(s)<br>");
        msg.append("There are " + ACTIVE_PLAYERS + " active players, and " + AUTHENTICATED_PLAYERS + " authenticated players<br>");
        msg.append("Daily player peak count: " + PlayerManager.getInstance().getDailyPlayerPeak() + "<br>");
        msg.append("<br>");

        player.send(new ALERT(msg.toString()));
    }

    @Override
    public String getDescription() {
        return "Get the uptime of the server";
    }
}
