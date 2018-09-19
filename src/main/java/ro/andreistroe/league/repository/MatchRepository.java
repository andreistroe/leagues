package ro.andreistroe.league.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ro.andreistroe.league.model.Match;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long>
{

}
