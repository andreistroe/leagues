package ro.andreistroe.league.resource;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import ro.andreistroe.league.exception.LeagueNotFoundException;
import ro.andreistroe.league.model.League;
import ro.andreistroe.league.repository.LeagueRepository;

@RestController
public class LeagueResource
{
    @Autowired
    private LeagueRepository leagueRepository;

    @GetMapping("/leagues")
    public List<League> getLeagues()
    {
        return leagueRepository.findAll();
    }

    @GetMapping("/league/{id}")
    public League retrieveLeague(@PathVariable long id) throws LeagueNotFoundException
    {
        Optional<League> league = leagueRepository.findById(id);
        if (!league.isPresent())
        {
            throw new LeagueNotFoundException(String.format("id-%d", id));
        }

        return league.get();
    }

    @DeleteMapping("/league/{id}")
    public void deleteLeague(@PathVariable long id)
    {
        leagueRepository.deleteById(id);
    }

    @PostMapping("/league")
    public ResponseEntity<League> createLeague(@RequestBody League league)
    {
        League savedLeague = leagueRepository.save(league);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedLeague.getId()).toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/league/{id}")
    public ResponseEntity<Object> updateLeague(@RequestBody League league, @PathVariable long id)
    {
        Optional<League> leagueOptional = leagueRepository.findById(id);
        if (!leagueOptional.isPresent())
        {
            return ResponseEntity.notFound().build();
        }
        league.setId(id);
        leagueRepository.save(league);
        return ResponseEntity.noContent().build();
    }
}
