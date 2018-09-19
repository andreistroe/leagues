package ro.andreistroe.league.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import ro.andreistroe.league.model.Match;
import ro.andreistroe.league.model.TeamSeason;
import ro.andreistroe.league.repository.MatchRepository;

@Controller
public class MatchController
{
    @Autowired
    private MatchRepository matchRepository;

    @RequestMapping(value = "/ui/flipMatch/{id}")
    public ModelAndView flipMatch(Model model, @PathVariable(required = true, name = "id") Long matchId)
    {
        Match match = matchRepository.findById(matchId).get();
        TeamSeason oldHosts = match.getHosts();
        match.setHosts(match.getGuests());
        match.setGuests(oldHosts);
        Integer oldHostsGoals = match.getHostsGoals();
        match.setHostsGoals(match.getGuestsGoals());
        match.setGuestsGoals(oldHostsGoals);
        
        matchRepository.save(match);
        
        return new ModelAndView(String.format("forward:/ui/viewMatches/%d", match.getDay().getLeague().getId()));
    }
}
