package com.nasa.hackaton;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

public class ThermoSolver {

	public Edge psi1;
	public Edge psi2;
	public Edge chi1;
	public Edge chi2;

	public Edge phi0;

    public double dx, dy, a, b;
    public int xNodes, yNodes;

    public double alpha;
    public double dt;

    public List<Array2> timeLayer = new ArrayList<Array2>();

    public double t = 0;

    public double maxValue = 0;

	public ThermoSolver(double _alpha) {
		alpha = _alpha;
	}

	public ThermoSolver(Edge _psi1, Edge _psi2, Edge _chi1, Edge _chi2, Edge _phi0) {
		psi1 = _psi1;
		psi2 = _psi2;
		chi1 = _chi1;
		chi2 = _chi2;

		phi0 = _phi0;
	}

	private double u(int n, int i, int j) {
		// starter
        if (n == 0) return phi0.eval(i * dx, j * dy);

        // borders
        if (j == 0) return psi1.eval(i * dx, n * dt);
        if (j == yNodes - 1) return psi2.eval(i * dx, n * dt); // a

        if (i == 0) return chi1.eval(j * dy, n * dt);
        if (i == xNodes - 1) return chi2.eval(j * dy, n * dt); // b

        return timeLayer.get(n).data[i][j];
    }

    private double bounds(int n, int i, int j) {
        if (j == 0) return psi1.eval(i * dx, n * dt);
        if (j == yNodes - 1) return psi2.eval(i * dx, n * dt); // a

        if (i == 0) return chi1.eval(j * dy, n * dt);
        if (i == xNodes - 1) return chi2.eval(j * dy, n * dt); // b

        return 0;
    }

    public void reset()
    {
        timeLayer = new ArrayList<Array2>();
        timeLayer.add(new Array2(emptyLayer()));
        maxValue = 0;

        t = 0;
    }

    public double[][] emptyLayer() {
        double[][] layer = new double[xNodes][yNodes];

        for (int i = 0; i < xNodes; i++) {
            for (int j = 0; j < yNodes; j++) {
                layer[i][j] = bounds(1, i, j);
            }
        }

        return layer;
    }

    public double stableTimeStep(double dx, double dy) {
        double maxStep = 0.5 / (1 / (dx * dx) + 1 / (dy * dy));
        return (maxStep - Math.abs(maxStep / 3));
    }

    public void setGrid(double _dx, double _dy, double _a, double _b) {
        dx = _dx; dy = _dy;
        a = _a; b = _b;

        xNodes = (int)Math.ceil(a / dx);
        yNodes = (int)Math.ceil(b / dy);

        dt = stableTimeStep(dx, dy);

        timeLayer.add(new Array2(emptyLayer()));
    }

    private void setMax(double v) {
        if (v > maxValue) maxValue = v;
    }

    public void simpleExplicitNext() {
        int n = timeLayer.size() - 1;
        timeLayer.add(new Array2(emptyLayer()));

        for (int i = 1; i < xNodes - 1; i++) {
            for (int j = 1; j < yNodes - 1; j++) {
            	timeLayer.get(n+1).data[i][j] =
                    u(n, i, j) + dt * alpha * ( (u(n, i + 1, j) - 2 * u(n, i, j) + u(n, i - 1, j)) / (dx * dx) +
                                                (u(n, i, j + 1) - 2 * u(n, i, j) + u(n, i, j - 1)) / (dy * dy)   );

                //if (timeLayer[n + 1][i, j] > maxValue) maxValue = timeLayer[n + 1][i, j];
                setMax(timeLayer.get(n+1).data[i][j]);
            }
        }

        t += dt;
    }

    /* Crank-Nicolson scheme, Anderson D., page 139 */

    double crA() {
        return -(alpha * dt) / (2 * dy * dy);
    }

    double crB() {
        return -(alpha * dt) / (2 * dx * dx);
    }

    double crC() {
        return 1 - 2 * crA() - 2 * crB();
    }

    private double delta2Xu(int n, int i, int j) {
        return (u(n, i + 1, j) - 2 * u(n, i, j) + u(n, i - 1, j)) / (dx * dx);
    }

    private double delta2Yu(int n, int i, int j) {
        return (u(n, i, j + 1) - 2 * u(n, i, j) + u(n, i, j - 1)) / (dy * dy);
    }

    private double d(int n, int i, int j) {
        return u(n, i, j) + (delta2Xu(n, i, j) + delta2Yu(n, i, j)) * alpha * dt / 2;
    }

    boolean checkBounds(int i, int j) {
        boolean res = false;
        if (i == 0 || i == xNodes - 1 || j == 0 || j == yNodes - 1) res = true;
        return res;
    }

    private double rightPart(int i, int j, int n) {
        double right = d(n, i, j);

        if (checkBounds(i, j - 1))  right -= crA() * u(n, i, j - 1);
        if (checkBounds(i - 1, j))  right -= crB() * u(n, i - 1, j);
        if (checkBounds(i, j))      right -= crC() * u(n, i, j);
        if (checkBounds(i + 1, j))  right -= crB() * u(n, i + 1, j);
        if (checkBounds(i, j + 1))  right -= crA() * u(n, i, j + 1);

        return right;
    }


    // Anderson D., page 141

    public double[][] crankNicoslonMatrix() {
        int z = Math.max(xNodes, yNodes);
        int crN = (xNodes - 2) * (yNodes - 2);

        double[][] m = new double[crN][crN];

        // left matrix, c diagonal
        for (int i = 0; i < crN; i++) {
            m[i][i] = crC();
        }

        // left matrix, b diagonal
        for (int i = 0; i + 1 < crN; i++) {
            if ((i + 1) % (z - 2) != 0) {
                m[i + 1][i] = m[i][i + 1] = crB();
            }
        }

        // left matrix, a diagonal
        int s = z - 2;
        for (int i = 0; i < crN - s; i++) {
            m[s + i][i] = m[i][s + i] = crA();
        }

        return m;
    }

    public double[] crankNicolsonRightVector(int n) {
        int z = Math.max(xNodes, yNodes);
        int crN = (xNodes - 2) * (yNodes - 2);

        double[] r = new double[crN];

        int k = 0;
        for (int i = 1; i < xNodes - 1; i++) {
            for (int j = 1; j < yNodes - 1; j++) {
                r[k] = rightPart(i, j, n); k++;
            }
        }

        return r;
    }

    public double[] matrixVectorMultiplication(double[][] m, double[] v) {
        double[] res = new double[v.length];

        for (int i = 0; i < res.length; i++) {
            for (int j = 0; j < res.length; j++) {
                res[i] += v[j] * m[i][j];
            }
        }

        return res;
    }

    public void crankNicolsonNext() {
        double[][] left = crankNicoslonMatrix();

        double[] right = crankNicolsonRightVector(timeLayer.size() - 1);

        RealMatrix m = MatrixUtils.createRealMatrix(left);
        m = new LUDecomposition(m).getSolver().getInverse();

        double[] c = matrixVectorMultiplication(left, right);

        double[][] layer = new double[xNodes][yNodes];

        int k = 0;
        for (int i = 0; i < xNodes; i++)
        {
            for (int j = 0; j < yNodes; j++)
            {
                if (checkBounds(i, j)) {
                    layer[i][j] = bounds(timeLayer.size() - 1, i, j);
                } else {
                    layer[i][j] = c[k]; k++;
                }

                setMax(layer[i][j]);
            }
        }

        timeLayer.add(new Array2(layer));
    }
}
