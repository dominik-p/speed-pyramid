package eu.speedbadminton.pyramid.listener;

import eu.speedbadminton.pyramid.model.Match;
import eu.speedbadminton.pyramid.service.MatchService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * User: Yoann Moranville
 * Date: 05/11/2013
 *
 * @author Yoann Moranville
 */
public class InactivityTask {
    private static final Logger LOGGER = Logger.getLogger(InactivityTask.class);
    private long duration;
    private static final Duration INTERVAL = new Duration(0, 2, 0);
    private static final Duration MAX_TIME_TO_PLAY = new Duration(21*24, 0, 0); //21 days

    @Autowired
    private MatchService matchService;

    public InactivityTask() {}

    protected void run() {
        LOGGER.info("Inactivity process active");
        long endTime = System.currentTimeMillis() + duration;
        boolean stopped = false;
        while (!stopped && System.currentTimeMillis() < endTime) {
            try {
                process(endTime);
            } catch (Throwable e) {
                LOGGER.error("Error: " + e.getMessage(), e);
                stopped = true;
            }
            if (!stopped && (System.currentTimeMillis() + INTERVAL.getMilliseconds()) < endTime) {
                try {
                    Thread.sleep(INTERVAL.getMilliseconds());
                } catch (InterruptedException e) {
                }
            } else {
                stopped = true;
            }
        }
        LOGGER.info("Inactivity process inactive");
    }

    public boolean process(long endTime) throws Exception {
        List<Match> matches = matchService.getMatches();
        for(Match match : matches) {
            if(System.currentTimeMillis() > endTime) {
                break;
            }
            LOGGER.info("Checking if this match has not been played yet: " + match.toString());
            if(match.getMatchDate() == null) {
                LOGGER.info("This match has not been played yet");
                if(match.getCreation().getTime() + MAX_TIME_TO_PLAY.getMilliseconds() <= System.currentTimeMillis()) {
                    LOGGER.info("Match has not been played in time... We need to switch places between both players and send both an email.");

                }
            }
        }
        return false;
    }

    public void setDuration(long duration) {
        this.duration = duration * 60l * 1000l;
    }
}