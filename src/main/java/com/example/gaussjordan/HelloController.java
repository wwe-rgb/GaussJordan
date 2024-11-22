package com.example.gaussjordan;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.text.DecimalFormat;

public class HelloController {
    @FXML
    private GridPane matrixGrid;
    @FXML
    private TextArea resultArea;
    @FXML
    private TextArea procedureArea;
    @FXML
    private Label timeLabel;
    @FXML
    private Spinner<Integer> sizeSpinner;
    @FXML
    private Button generateButton;
    @FXML
    private Button calculateButton;

    private TextField[][] coefficientFields;
    private TextField[] constantFields;
    private DecimalFormat df = new DecimalFormat("#.####");
    private Timeline timeline;

    @FXML
    public void initialize() {
        // Configurar el spinner
        sizeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 6, 3));

        // Configurar botones
        generateButton.setOnAction(e -> generateMatrix());
        calculateButton.setOnAction(e -> calculateGaussJordan());

        // Iniciar actualizador de tiempo
        startTimeUpdater();

        // Generar matriz inicial
        generateMatrix();
    }

    private void startTimeUpdater() {
        updateTime(); // Actualización inicial
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateTime()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        timeLabel.setText("Fecha y Hora: " + now.format(formatter));
    }

    private void generateMatrix() {
        matrixGrid.getChildren().clear();
        int size = sizeSpinner.getValue();

        coefficientFields = new TextField[size][size];
        constantFields = new TextField[size];

        // Etiquetas de variables
        for (int i = 0; i < size; i++) {
            matrixGrid.add(new Label("x" + (i + 1)), i + 1, 0);
        }
        matrixGrid.add(new Label("="), size + 1, 0);
        matrixGrid.add(new Label("b"), size + 2, 0);

        // Crear campos para coeficientes y constantes
        for (int i = 0; i < size; i++) {
            matrixGrid.add(new Label("Ecuación " + (i + 1) + ":"), 0, i + 1);

            for (int j = 0; j < size; j++) {
                TextField field = createNumericTextField();
                coefficientFields[i][j] = field;
                matrixGrid.add(field, j + 1, i + 1);

                if (j < size - 1) {
                    Label plusLabel = new Label("+");
                    plusLabel.setPadding(new Insets(0, 5, 0, 5));
                    matrixGrid.add(plusLabel, j + 2, i + 1);
                }
            }

            TextField constantField = createNumericTextField();
            constantFields[i] = constantField;
            matrixGrid.add(constantField, size + 2, i + 1);
        }
    }

    private TextField createNumericTextField() {
        TextField field = new TextField("0");
        field.setPrefWidth(60);
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("-?\\d*\\.?\\d*")) {
                field.setText(oldValue);
            }
        });
        return field;
    }

    @FXML
    private void calculateGaussJordan() {
        try {
            int size = sizeSpinner.getValue();
            double[][] matrix = new double[size][size + 1];

            // Obtener valores de los campos
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    matrix[i][j] = Double.parseDouble(coefficientFields[i][j].getText());
                }
                matrix[i][size] = Double.parseDouble(constantFields[i].getText());
            }

            StringBuilder procedure = new StringBuilder();
            procedure.append("Matriz original:\n");
            appendMatrix(procedure, matrix);

            // Aplicar método de Gauss-Jordan
            for (int i = 0; i < size; i++) {
                // Pivoteo
                double pivot = matrix[i][i];
                if (Math.abs(pivot) < 1e-10) {
                    throw new ArithmeticException("El pivote es cero, la matriz no tiene solución única");
                }

                // Normalizar fila del pivote
                for (int j = 0; j <= size; j++) {
                    matrix[i][j] /= pivot;
                }

                procedure.append("\nNormalización de fila ").append(i + 1).append(":\n");
                appendMatrix(procedure, matrix);

                // Eliminar variable de otras filas
                for (int k = 0; k < size; k++) {
                    if (k != i) {
                        double factor = matrix[k][i];
                        for (int j = 0; j <= size; j++) {
                            matrix[k][j] -= factor * matrix[i][j];
                        }
                    }
                }

                procedure.append("\nEliminación de variable en otras filas:\n");
                appendMatrix(procedure, matrix);
            }

            // Mostrar resultado
            StringBuilder result = new StringBuilder("Solución:\n");
            for (int i = 0; i < size; i++) {
                result.append("x").append(i + 1).append(" = ")
                        .append(df.format(matrix[i][size])).append("\n");
            }

            resultArea.setText(result.toString());
            procedureArea.setText(procedure.toString());

        } catch (Exception e) {
            resultArea.setText("Error: " + e.getMessage());
            procedureArea.setText("");
        }
    }

    private void appendMatrix(StringBuilder sb, double[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                sb.append(df.format(matrix[i][j])).append("\t");
            }
            sb.append("\n");
        }
    }
}