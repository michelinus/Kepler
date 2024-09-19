package org.alexdev.kepler.game.commands.registered;

import org.alexdev.kepler.game.commands.Command;
import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.fuserights.Fuseright;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.user.ALERT;
import org.alexdev.kepler.util.DateUtil;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

public class MemoryCommand extends Command {
    private static final int UPTIME_COMMAND_INTERVAL_SECONDS = 5;
    private static long UPTIME_COMMAND_EXPIRY = 0L;

    private static final int CPU_NUM_THREADS = Runtime.getRuntime().availableProcessors();
    private static final String JVM_NAME = System.getProperty("java.vm.name");
    private static final String JVM_VERSION = System.getProperty("java.version");
    private static final String JVM_VENDOR = System.getProperty("java.vendor");
    private static final String OS = System.getProperty("os.name") + " " + System.getProperty("os.version") + " " + System.getProperty("os.arch");

    private static int MEMORY_USAGE = 0;
    private static int TOTAL_MEMORY = 0;
    private static int CPU_LOAD = 0;
    private OperatingSystemMXBean osBean = null;

    public MemoryCommand(){
        UPTIME_COMMAND_EXPIRY = DateUtil.getCurrentTimeSeconds();
        TOTAL_MEMORY = (int) Runtime.getRuntime().totalMemory() / 1024 / 1024;
        osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
    }

    @Override
    public void addPermissions() {
        this.permissions.add(Fuseright.ADMINISTRATOR_ACCESS);
    }

    @Override
    public void handleCommand(Entity entity, String message, String[] args) {
        if (entity.getType() != EntityType.PLAYER) {
            return;
        }

        Player player = (Player) entity;

        if (DateUtil.getCurrentTimeSeconds() > UPTIME_COMMAND_EXPIRY) {
            Runtime runtime = Runtime.getRuntime();
            MEMORY_USAGE = (int) ((runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024);
            CPU_LOAD = (int) osBean.getCpuLoad();

            UPTIME_COMMAND_EXPIRY = DateUtil.getCurrentTimeSeconds() + UPTIME_COMMAND_INTERVAL_SECONDS;
        }

        StringBuilder msg = new StringBuilder();
        msg.append("SYSTEM INFO<br>");
        msg.append("Load: " + CPU_LOAD + "%, Cores: " + CPU_NUM_THREADS + "<br>");
        msg.append("RAM: Used: " + MEMORY_USAGE + "MB / Max: "+ TOTAL_MEMORY + "MB (jvm process)<br>");
        msg.append("SO: " + OS + "<br>");
        msg.append("----------------" + "<br>");
        msg.append("JAVA VIRTUAL MACHINE<br>");
        msg.append("Name: " + JVM_NAME + "<br>");
        msg.append("Version: " + JVM_VERSION + "<br>");
        msg.append("Vendor: " + JVM_VENDOR);

        player.send(new ALERT(msg.toString()));
    }

    @Override
    public String getDescription() {
        return "Get accurate memory usage of the server";
    }
}
