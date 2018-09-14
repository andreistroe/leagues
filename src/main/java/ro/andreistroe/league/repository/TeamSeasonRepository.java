package ro.andreistroe.league.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ro.andreistroe.league.model.TeamSeason;

public interface TeamSeasonRepository extends JpaRepository<TeamSeason, Long>
{

}
