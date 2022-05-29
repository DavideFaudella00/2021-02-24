package it.polito.tdp.PremierLeague.model;

import java.util.PriorityQueue;

import it.polito.tdp.PremierLeague.model.Event.EventType;

public class Simulator {
	Model model = new Model();
	// private Player bestplayer = model.getBest();
	private int goalA = 0;
	private int goalB = 0;
	private int espulsiA = 0;
	private int espulsiB = 0;

	private double pGoal = 0.5;
	private double pEspulsi = 0.3;
	private double pInfortuni = 0.2;
	private double pEspulsiBest = 0.6;
	private double pEspulsiWors = 1 - pEspulsiBest;

	private PriorityQueue<Event> queue;

	private void creaEventi(int numEventi) {
		EventType tipo = null;
		for (int i = 0; i < numEventi; i++) {
			double rand = Math.random();
			if (rand < pInfortuni) {
				tipo = EventType.INFORTUNIO;
			} else if (rand < pEspulsi + pInfortuni) {
				tipo = EventType.ESPULSIONE;
			} else if (rand < pEspulsi + pInfortuni + pGoal) {
				tipo = EventType.GOAL;
			}
			Event e = new Event(tipo);
			queue.add(e);
		}
	}

	public void init(int numero) {
		this.queue = new PriorityQueue<Event>();
		creaEventi(numero);
	}

	public String run() {
		while (!queue.isEmpty()) {
			Event e = queue.poll();
			processaEvento(e);
		}
		return "Partita finita " + goalA + " - " + goalB + " e con " + espulsiA
				+ "giocatori espulsi per la squadra A e " + espulsiB + " espulsi per la squadra B";
	}

	private void processaEvento(Event e) {
		boolean squadra = false; // miglior giocatore in team A
		switch (e.getType()) {
		case ESPULSIONE:
			if (Math.random() < 0.5) {
				squadra = true; // miglior giocatore in team B
			}
			if (squadra == true) {
				if (Math.random() < 0.6) {
					espulsiB++;
				} else {
					espulsiA++;
				}
			} else if (squadra == false) {
				if (Math.random() < 0.6) {
					espulsiA++;
				} else {
					espulsiB++;
				}
			}
			break;
		case GOAL:
			if (espulsiA > espulsiB) {
				goalB++;
			} else {
				goalA++;
			}
			break;
		case INFORTUNIO:
			if (Math.random() > 0.5) {
				creaEventi(2);
			} else {
				creaEventi(3);
			}
			break;
		default:
			System.out.println("Errore");
			break;

		}

	}

}
