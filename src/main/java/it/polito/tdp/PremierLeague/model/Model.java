package it.polito.tdp.PremierLeague.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	PremierLeagueDAO dao;
	Graph<Player, DefaultWeightedEdge> grafo;
	Map<Integer, Player> idMap;

	public Model() {
		dao = new PremierLeagueDAO();
	}

	public List<Match> getAllMatches() {
		return dao.listAllMatches();
	}

	public void CreaGrafo(Match m) {
		grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		idMap = new HashMap<>();
		for (Player p : dao.listAllPlayers()) {
			idMap.put(p.getPlayerID(), p);
		}
		Graphs.addAllVertices(grafo, dao.getVertex(idMap, m));
		System.out.println("vertici: " + grafo.vertexSet().size());

		for (Adiacenza a : dao.getAdiacenze(m, idMap)) {
			if (a.getPeso() >= 0) {
				Graphs.addEdge(grafo, a.getP1().getP1(), a.getP2().getP1(), a.getPeso());
			} else {
				Graphs.addEdge(grafo, a.getP2().getP1(), a.getP1().getP1(), -a.getPeso());
			}
		}

	}

	private Player best = null;

	public String getGiocatore() {
		String result = "";
		double r = 0;
		double max = 0;

		for (Player p : grafo.vertexSet()) {
			r = 0;
			for (Player o : Graphs.neighborListOf(grafo, p)) {
				DefaultWeightedEdge d = grafo.getEdge(p, o);
				if (d != null) {
					r += grafo.getEdgeWeight(d);
				}
				DefaultWeightedEdge d1 = grafo.getEdge(o, p);
				if (d1 != null) {
					System.out.println("peso: " + grafo.getEdgeWeight(d1));
					r -= grafo.getEdgeWeight(d1);
				}
			}
			if (r > max) {
				max = r;
				best = p;
			}
		}
		result = best.getPlayerID() + " - " + best.getName() + ", " + max;

		return result;
	}

	public Player getBest() {
		return best;
	}

	private Simulator sim;

	public void init(int numero) {
		sim = new Simulator();
		sim.init(numero);
	}

	public String simula() {
		String s = sim.run();
		return s;
	}

}
