package ch.fha.ia02.above;

/**
 * A benchmark for the model.
 */
public class ModelBenchmark {

	private ModelFactory factory;
	private int n;
	private int dt;
	private Result result;

	/**
	 * Creates a new model benchmark.
	 *
	 * @param factory to create the model from.
	 * @param n number of runs in this test.
	 * @param dt stepwidth for each step.
	 */
	public ModelBenchmark(ModelFactory factory, int n, int dt) {
		if (factory == null) throw new IllegalArgumentException("Factory must not be null!");
		if (n <= 0) throw new IllegalArgumentException("Number of test runs must be positive!");
		this.factory = factory;
		this.n = n;
		this.dt = dt;
	}

	/**
	 * Runs the benchmark.
	 * This will create a new model using the specified factory
	 * and run the model <var>n</var> times.
	 */
	public Result run() {
		Model m = factory.createModel();
		long start = System.currentTimeMillis();
		for (int i=0; i<n; i++) {
			m.compute(dt);
		}
		result = new Result(m, n, dt, (int)(System.currentTimeMillis() - start));
		return result;
	}

	public String toString() {
		if (result != null) return result.toString();
		return getClass().getName()
			+ "[factory=" + factory
			+ ", n=" + n
			+ ", dt=" + dt
			+ "]";
	}

	/**
	 * Entry point to run the benchmark from the command line.
	 */
	public static void main(String[] args) {
		Application.getSettings().setDetailedShapes(false);
		ModelFactory factory = new SimpleModelFactory();
		ModelBenchmark bm = new ModelBenchmark(factory, 10000, 40);
		System.out.println("Benchmarking " + Application.getTitle());
		System.out.println(bm.factory);
		System.out.println(bm.n + " steps of " + bm.dt + " ms");
		for (int i=0; i<5; i++) {
			System.out.println(bm.run());
		}
	}


	/** Stores model benchmark results. */
	private static class Result {
		private int agents;
		private int steps;
		private int dt;
		private int runtime;
		private float avg;

		/** Creates a new benchmark result object. */
		public Result(Model model, int steps, int dt, int runtime) {
			this.agents = model.numAgents();
			this.steps = steps;
			this.dt = dt;
			this.runtime = runtime;
			this.avg = ((float)runtime)/steps;
		}

		/** Returns a string representation of this benchmark result. */
		public String toString() {
			return agents + " agents, "
				+ steps + " steps, "
				+ runtime + " ms (" + avg + " ms/step)";
		}
	}
}
