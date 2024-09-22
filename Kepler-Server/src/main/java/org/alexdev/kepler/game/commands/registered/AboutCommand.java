package org.alexdev.kepler.game.commands.registered;

import org.alexdev.kepler.Kepler;
import org.alexdev.kepler.game.commands.Command;
import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.fuserights.Fuseright;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.user.ALERT;

public class AboutCommand extends Command {

    @Override
    public void addPermissions() {
        this.permissions.add(Fuseright.DEFAULT);
    }

    @Override
    public void handleCommand(Entity entity, String message, String[] args) {
        if (entity.getType() != EntityType.PLAYER) {
            return;
        }

        Player player = (Player)entity;

        player.send(new ALERT("Oculus Emu " + Kepler.OCULUS_VERSION +" - HH: v14" +
                "<br>Based on Project Kepler rev. " + Kepler.SERVER_VERSION +
                "<br>" +
                "<br>Oculus contributors:" +
                "<br> - Michelinus" +
                "<br>" +
                "<br>Kepler contributors:" +
                "<br> - Quackster, ThuGie, Webbanditten, Ascii, Sefhriloff, Copyright" + // Call for help
                "<br> - Raptosaur, Hoshiko, Romuald, Glaceon, Nillus, Holo Team, Meth0d" +
                "<br> - office.boy, killerloader, Alito, wackfx" +
                "<br>" ));
    }

    @Override
    public String getDescription() {
        return " Information about the software powering this retro";
    }
}
