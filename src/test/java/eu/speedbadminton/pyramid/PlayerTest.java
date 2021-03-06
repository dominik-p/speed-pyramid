package eu.speedbadminton.pyramid;

import eu.speedbadminton.pyramid.model.Player;
import eu.speedbadminton.pyramid.service.PlayerService;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * User: Yoann Moranville
 * Date: 25/06/2013
 *
 * @author Yoann Moranville
 */


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/context-base.xml"})
public class PlayerTest {
    private static final Logger LOG = Logger.getLogger(PlayerTest.class);

    @Autowired
    private PlayerService playerService;

    @Test
    public void testReadPlayers() {
        List<Player> players = playerService.getPlayers();
        LOG.info("All players (" + players.size() + ")");
        for(Player player : players) {
            LOG.info("Player name: " + player.getName());
        }
        players = playerService.getEnabledPlayers();
        LOG.info("Only enabled players (" + players.size() + ")");
        for(Player player : players) {
            LOG.info("Player name: " + player.getName());
        }
    }
}
