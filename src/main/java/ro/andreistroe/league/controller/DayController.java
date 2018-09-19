package ro.andreistroe.league.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import ro.andreistroe.league.dto.LeagueDaysMatchesDto;
import ro.andreistroe.league.model.League;
import ro.andreistroe.league.repository.LeagueRepository;

@Controller
public class DayController
{
    @Autowired
    LeagueRepository leagueRepo;

    @RequestMapping(value = "/ui/viewMatches/{leagueId}")
    public String displayMatches(Model model, @PathVariable(required = true, name = "leagueId") Long leagueId)
    {
        League l = leagueRepo.findById(leagueId).get();
        LeagueDaysMatchesDto dto = new LeagueDaysMatchesDto();
        dto.setId(leagueId);
        dto.setDays(l.getDays());
        model.addAttribute("league", dto);
        return "editMatches";
    }

    @RequestMapping(method = RequestMethod.POST, value="/ui/saveMatches")
    public ModelAndView saveMatches(Model model, LeagueDaysMatchesDto dto)
    {
        League league = leagueRepo.findById(dto.getId()).get();
        league.getDays().clear();
        dto.getDays().stream().forEach(l -> l.setLeague(league));
        league.getDays().addAll(dto.getDays());
        leagueRepo.save(league);
        return new ModelAndView(String.format("redirect:/ui/editLeague/%d", dto.getId()));
    }
}
