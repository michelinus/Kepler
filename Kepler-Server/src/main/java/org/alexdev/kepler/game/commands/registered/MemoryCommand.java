package org.alexdev.kepler.game.commands.registered;

import com.sun.management.OperatingSystemMXBean;
import org.alexdev.kepler.game.commands.Command;
import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.fuserights.Fuseright;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.user.ALERT;
import org.alexdev.kepler.util.DateUtil;

import java.lang.management.ManagementFactory;

public class MemoryCommand extends Command {
    private static final int UPTIME_COMMAND_INTERVAL_SECONDS = 5;
    private static final int CPU_NUM_THREADS = Runtime.getRuntime().availableProcessors();
    private static final String JVM_NAME = System.getProperty("java.vm.name");
    private static final String JVM_VERSION = System.getProperty("java.version");
    private static final String JVM_VENDOR = System.getProperty("java.vendor");
    private static final String OS = System.getProperty("os.name") + " " + System.getProperty("os.version") + " " + System.getProperty("os.arch");
    private static long UPTIME_COMMAND_EXPIRY = 0L;
    private static int MEMORY_USAGE = 0;
    private static int TOTAL_MEMORY = 0;
    private static int CPU_LOAD = 0;
    private final OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

    public MemoryCommand() {
        UPTIME_COMMAND_EXPIRY = DateUtil.getCurrentTimeSeconds();
        TOTAL_MEMORY = (int) osBean.getTotalMemorySize() / 1024 / 1024;
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

        String msg = "SYSTEM INFO<br>" +
                "Load: " + CPU_LOAD + "%, Cores: " + CPU_NUM_THREADS + "<br>" +
                "RAM: Used: " + MEMORY_USAGE + "MB / Max: " + TOTAL_MEMORY + "MB (jvm process)<br>" +
                "SO: " + OS + "<br>" +
                "----------------" + "<br>" +
                "JAVA VIRTUAL MACHINE<br>" +
                "Name: " + JVM_NAME + "<br>" +
                "Version: " + JVM_VERSION + "<br>" +
                "Vendor: " + JVM_VENDOR;

        player.send(new ALERT(msg));
    }

    @Override
    public String getDescription() {
        return "Get accurate info usage of the server";
    }
}
