import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class PainelCovid19 extends JFrame {
    private JComboBox<String> tabelaComboBox;
    private JTextArea resultadoTextArea;

    public PainelCovid19() {
        setTitle("Painel COVID-19");
        setSize(800, 600);  // Ajustado o tamanho para acomodar o gráfico e a área de texto
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        tabelaComboBox = new JComboBox<>(new String[]{
            "Cidades", "Vacinas", "Mortes", "Hospitais", "Pacientes", "Testes",
            "Tratamentos", "Casos", "Sintomas", "Laboratorios", "Medicos", "Estatisticas", "Quarentenas"
        });
        tabelaComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exibirDados((String) tabelaComboBox.getSelectedItem());
            }
        });

        resultadoTextArea = new JTextArea();
        resultadoTextArea.setEditable(false);

        // Adiciona os componentes ao painel principal
        add(tabelaComboBox, BorderLayout.NORTH);
        add(new JScrollPane(resultadoTextArea), BorderLayout.CENTER);
        add(createChartPanel(), BorderLayout.SOUTH);  // Adiciona o painel do gráfico
    }

    private void exibirDados(String tabela) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/covid19", "root", "#!@0Mi4$Uszenfone");
            System.out.println("Conexão estabelecida com sucesso!");

            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM " + tabela);

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            StringBuilder resultado = new StringBuilder();

            for (int i = 1; i <= columnCount; i++) {
                resultado.append(metaData.getColumnName(i)).append("\t");
            }
            resultado.append("\n");

            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    resultado.append(resultSet.getString(i)).append("\t");
                }
                resultado.append("\n");
            }

            if (resultado.length() == 0) {
                resultado.append("Nenhum dado encontrado para a tabela ").append(tabela).append(".");
            }

            resultadoTextArea.setText(resultado.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            resultadoTextArea.setText("Erro ao conectar ou consultar o banco de dados: " + e.getMessage());
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private JPanel createChartPanel() {
        // Dados do gráfico (exemplo: 70% vacinados, 30% não vacinados)
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Vacinados", 70);
        dataset.setValue("Não vacinados", 30);

        // Criação do gráfico de pizza
        JFreeChart pieChart = ChartFactory.createPieChart(
                "Status de Vacinação",
                dataset,
                true, true, false);

        // Personalização do gráfico
        PiePlot plot = (PiePlot) pieChart.getPlot();
        plot.setSectionPaint("Vacinados", new Color(76, 175, 80)); // Verde
        plot.setSectionPaint("Não vacinados", new Color(244, 67, 54)); // Vermelho
        plot.setExplodePercent("Vacinados", 0.10);

        // Painel do gráfico
        ChartPanel chartPanel = new ChartPanel(pieChart);
        chartPanel.setPreferredSize(new Dimension(600, 400));

        return chartPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PainelCovid19().setVisible(true);
            }
        });
    }
}
