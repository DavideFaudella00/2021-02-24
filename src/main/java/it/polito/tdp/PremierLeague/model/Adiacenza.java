package it.polito.tdp.PremierLeague.model;

public class Adiacenza {
	private EfficienzaGiocatore p1;
	private EfficienzaGiocatore p2;
	private double peso;

	public Adiacenza(EfficienzaGiocatore p1, EfficienzaGiocatore p2) {
		this.p1 = p1;
		this.p2 = p2;
		this.peso = p1.getEfficienza() - p2.getEfficienza();
	}

	public EfficienzaGiocatore getP1() {
		return p1;
	}

	public void setP1(EfficienzaGiocatore p1) {
		this.p1 = p1;
	}

	public EfficienzaGiocatore getP2() {
		return p2;
	}

	public void setP2(EfficienzaGiocatore p2) {
		this.p2 = p2;
	}

	public double getPeso() {
		return peso;
	}

	public void setPeso(double peso) {
		this.peso = peso;
	}

}
