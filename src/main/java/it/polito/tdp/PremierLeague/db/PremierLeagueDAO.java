package it.polito.tdp.PremierLeague.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mariadb.jdbc.internal.com.read.dao.Results;

import it.polito.tdp.PremierLeague.model.Action;
import it.polito.tdp.PremierLeague.model.Adiacenza;
import it.polito.tdp.PremierLeague.model.EfficienzaGiocatore;
import it.polito.tdp.PremierLeague.model.Match;
import it.polito.tdp.PremierLeague.model.Player;
import it.polito.tdp.PremierLeague.model.Team;

public class PremierLeagueDAO {

	public List<Player> listAllPlayers() {
		String sql = "SELECT * FROM Players";
		List<Player> result = new ArrayList<Player>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Player player = new Player(res.getInt("PlayerID"), res.getString("Name"));
				result.add(player);
			}
			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<Team> listAllTeams() {
		String sql = "SELECT * FROM Teams";
		List<Team> result = new ArrayList<Team>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Team team = new Team(res.getInt("TeamID"), res.getString("Name"));
				result.add(team);
			}
			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<Action> listAllActions() {
		String sql = "SELECT * FROM Actions";
		List<Action> result = new ArrayList<Action>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Action action = new Action(res.getInt("PlayerID"), res.getInt("MatchID"), res.getInt("TeamID"),
						res.getInt("Starts"), res.getInt("Goals"), res.getInt("TimePlayed"), res.getInt("RedCards"),
						res.getInt("YellowCards"), res.getInt("TotalSuccessfulPassesAll"),
						res.getInt("totalUnsuccessfulPassesAll"), res.getInt("Assists"),
						res.getInt("TotalFoulsConceded"), res.getInt("Offsides"));

				result.add(action);
			}
			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<Match> listAllMatches() {
		String sql = "SELECT m.MatchID, m.TeamHomeID, m.TeamAwayID,"
				+ " m.teamHomeFormation, m.teamAwayFormation, m.resultOfTeamHome," + " m.date, t1.Name, t2.Name   "
				+ "FROM Matches m, Teams t1, Teams t2 " + "WHERE m.TeamHomeID = t1.TeamID AND m.TeamAwayID = t2.TeamID";
		List<Match> result = new ArrayList<Match>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Match match = new Match(res.getInt("m.MatchID"), res.getInt("m.TeamHomeID"), res.getInt("m.TeamAwayID"),
						res.getInt("m.teamHomeFormation"), res.getInt("m.teamAwayFormation"),
						res.getInt("m.resultOfTeamHome"), res.getTimestamp("m.date").toLocalDateTime(),
						res.getString("t1.Name"), res.getString("t2.Name"));

				result.add(match);

			}
			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<Player> getVertex(Map<Integer, Player> idMap, Match m) {
		String sql = "SELECT a.PlayerID AS id " + "FROM actions a " + "WHERE a.MatchID = ?";
		List<Player> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, m.getMatchID());
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Player player = idMap.get(res.getInt("id"));
				result.add(player);
			}
			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<Adiacenza> getAdiacenze(Match m, Map<Integer, Player> idMap) {
		List<Adiacenza> result = new ArrayList<>();
		String sql = "SELECT a1.PlayerID as id1, a2.PlayerID as id2 " + "FROM actions a1, actions a2 "
				+ "WHERE a1.MatchID = ? AND a1.TeamID <> a2.TeamID "
				+ "AND a1.PlayerID > a2.PlayerID AND a1.MatchID = a2.MatchID";
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, m.getMatchID());
			ResultSet res = st.executeQuery();
			Map<Integer, EfficienzaGiocatore> ma = this.getEfficienze(m, idMap);
			while (res.next()) {
				Player p1 = idMap.get(res.getInt("id1"));
				Player p2 = idMap.get(res.getInt("id2"));
				EfficienzaGiocatore e1 = new EfficienzaGiocatore(p1, ma.get(p1.getPlayerID()).getEfficienza());
				EfficienzaGiocatore e2 = new EfficienzaGiocatore(p2, ma.get(p2.getPlayerID()).getEfficienza());
				Adiacenza a = new Adiacenza(e1, e2);
				result.add(a);
			}
			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Map<Integer, EfficienzaGiocatore> getEfficienze(Match m, Map<Integer, Player> idMap) {
		Map<Integer, EfficienzaGiocatore> result = new HashMap<>();
		String sql = "SELECT a.PlayerID AS id, " + "(a.TotalSuccessfulPassesAll + a.Assists) / a.TimePlayed AS ef "
				+ "FROM actions a " + "WHERE a.MatchID = ?";
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, m.getMatchID());
			ResultSet res = st.executeQuery();

			while (res.next()) {
				EfficienzaGiocatore e = new EfficienzaGiocatore(idMap.get(res.getInt("id")), res.getDouble("ef"));
				result.put(e.getP1().getPlayerID(), e);
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;

	}

}
