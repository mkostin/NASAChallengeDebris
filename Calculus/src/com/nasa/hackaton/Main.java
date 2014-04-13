package com.nasa.hackaton;

public class Main {

	public enum EvalMethod {
		SIMPLE, CRANK_NICOLSON;
	}

	public static void main(String[] args) {

		EvalMethod method = EvalMethod.CRANK_NICOLSON;
		ThermoSolver solver = new ThermoSolver(1.0);

		long step = 0;
		final long MAX_STEP = 1000;
		while (step < MAX_STEP) {
			process(method, solver);
			step++;
		}
	}

	public static void process(EvalMethod method, ThermoSolver solver) {
		try {
			// ht2d.computeNext();
			switch (method) {
			case SIMPLE:
				solver.simpleExplicitNext();
				break;
			case CRANK_NICOLSON:
				solver.crankNicolsonNext();
				break;
			}

			try {
				Thread.sleep(250);
			} catch (Exception e) {
			}
			Array2 layer = solver.timeLayer.get(solver.timeLayer.size() - 1);

			// Visualize

		} catch (Exception ex) {
		}
	}
}
