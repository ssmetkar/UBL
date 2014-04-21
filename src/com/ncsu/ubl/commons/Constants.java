package com.ncsu.ubl.commons;

public interface Constants {
	public static final String NUMBER_OF_VM = "number_of_vm";
	public static final String IP_ADDRESS = "ip_address";
	public static final String NEXT_ITERATION_TIME ="next_iteration_time";
	public static final String WEIGHT_NUMBER = "weight_number";
	public static final String WEB_SERVICE_PATH = "web_service_path";
	public static final String WEB_SERVICE_PORT = "web_service_port";
	public static final String HTTP_GET = "GET";
	public static final String OUTPUT_FILENAME = "output_filename";
	public static final String OUTPUT_FILEPATH = "output_file_path";
	public static final String SPLIT_CHAR = " ";
	public static final String LEARN_FILE_LOCATION = "learn_file_location";
	public static final String LEARN_FILE_NAME = "learn_file_name";
	public static final String TOPOLOGY_MODEL = "topology_model";
	public static final String ROWS = "rows";
	public static final String COLS = "cols";
	public static final String RADIUS = "radius";
	public static final String NUMBER_OF_WEIGHTS = "number_of_weights";
	public static final String MAX_WEIGHT = "max_weight";
	public static final String TRAIN_METRIC_TYPE = "train_metric_type";
	public static final String MAX_ITERATION = "max_iteration";
	public static final String PREDICT_METRIC_TYPE = "predict_metric_type";
	public static final String LEARN_FACTOR = "learning_factor";
	public static final String NEIGHBOUR_FACTOR = "neighbour_factor";
	public static final String K_FOLD_VALUE = "k_fold_value";
	public static final String DELIMITER = "delimiter";
	public static final String PYTHON_FILE = "python_file";
	public static final String NORMALIZED_FILE = "normalized_file";
	public static final String NORMAL_NEURONS = "normal_neurons";
	public static final int ABNORMAL = 1;
	public static final int NORMAL = 0;
	public static final String PREDICT_DATA_FILE = "predict_data_file";
	public static final String LOOK_AHEAD_SIZE = "look_ahead_size";
	
	public static enum METRIC {
		MEM(0),
		CPU(1),
        NETTX(2),
        NETRX(3),
        VBD_OO(4),
        VBD_RD(5),
        VBD_WR(6);
        
        private int value;

        private METRIC(int value) {
                this.setValue(value);
        }

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}
	};


}