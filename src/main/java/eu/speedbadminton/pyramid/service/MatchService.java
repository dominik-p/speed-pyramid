package eu.speedbadminton.pyramid.service;

import eu.speedbadminton.pyramid.model.Match;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * User: Yoann Moranville
 * Date: 26/06/2013
 *
 * @author Yoann Moranville
 */
@Service
@Repository
public class MatchService {

    @Autowired
    private MongoTemplate mongoTemplate;
    private static final String COLLECTION_NAME = "match";

    public void save(Match match) {
        if (!mongoTemplate.collectionExists(Match.class)) {
            mongoTemplate.createCollection(Match.class);
        }
        match.setId(UUID.randomUUID().toString());
        mongoTemplate.insert(match, COLLECTION_NAME);
    }

    public Match getMatchById(long matchId) {
        Criteria criteria = new Criteria("{_id:ObjectId(\"" + matchId + "\")}");
        Query query = new Query(criteria);
        return mongoTemplate.findOne(query, Match.class, COLLECTION_NAME);
    }

    public List<Match> getMatches() {
        return mongoTemplate.findAll(Match.class, COLLECTION_NAME);
    }

}
