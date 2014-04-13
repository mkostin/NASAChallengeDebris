package com.nasa.hackaton;

public class Edge {

	double x;
	double t;

	public Edge(double _x, double _t) {
		x = _x;
		t = _t;
	}

	public double eval(double _x, double _t){
		return _x + _t;
	}
}
