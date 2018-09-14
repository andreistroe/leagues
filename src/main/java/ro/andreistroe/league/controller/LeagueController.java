package ro.andreistroe.league.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.RedirectView;

import ro.andreistroe.league.model.League;
import ro.andreistroe.league.repository.LeagueRepository;

@Controller
public class LeagueController
{

    @Autowired
    LeagueRepository repo;

    @RequestMapping(value = "/")
    public String leagueList(Model model)
    {
        model.addAttribute("leagues", repo.findAll());
        return "leagueList";
    }

    @RequestMapping(value = { "/ui/editLeague", "/ui/editLeague/{id}" })
    public String editLeague(Model model, @PathVariable(required = false, name = "id") Long id)
    {
        model.addAttribute("league", null == id ? new League() : repo.findById(id).get());
        return "editLeague";
    }

    @RequestMapping(value = { "/ui/saveLeague" }, method = RequestMethod.POST)
    public RedirectView saveLeague(Model model, League league)
    {
        repo.save(league);
        model.addAttribute("leagues", repo.findAll());
        return new RedirectView("/");
    }

    @RequestMapping(value = { "/ui/deleteLeague/{id}" })
    public RedirectView deleteLeague(Model model, @PathVariable(required = true, name = "id") Long id)
    {
        Optional<League> leagueOpt = repo.findById(id);
        if (leagueOpt.isPresent())
        {
            League league = leagueOpt.get();
            if (league.getTeams().isEmpty() && league.getDays().isEmpty())
            {
                repo.delete(league);
            }
        }
        return new RedirectView("/");
    }
}
