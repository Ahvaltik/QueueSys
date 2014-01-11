/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package queuesys;

import java.awt.*;
import java.awt.event.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import queuesys.search.cockroaches.Cockroaches;
import queuesys.search.hybrid.Hybrid;
//import sun.awt.WindowClosingListener;

import javax.swing.*;

class Frame extends JFrame implements MouseListener, WindowListener {
    static enum Algorithm {
        NONE, COCKROACH, CUCKOO, HYBRID
    }

    /* dowolna liczba zmiennoprzecinkowa */
    static class DoubleVerifier extends InputVerifier {
        @Override
        public boolean verify(JComponent input) {
            try {
                Double.parseDouble(((JTextArea)input).getText());
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }

    /* nieujemna liczba zmiennoprzecinkowa (x >= 0) */
    static class NonNegativeDoubleVerifier extends InputVerifier {
        @Override
        public boolean verify(JComponent input) {
            try {
                return Double.parseDouble(((JTextArea)input).getText()) > 0.0;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }

    /* prawdopodobienstwo (0 <= x <= 1) */
    static class ProbabilityVerifier extends InputVerifier {
        @Override
        public boolean verify(JComponent input) {
            try {
                double val = Double.parseDouble(((JTextArea)input).getText());
                return val >= 0.0 && val <= 1.0;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }

    /* dodatnia liczba calkowita (x > 0) */
    static class PositiveIntVerifier extends InputVerifier {
        @Override
        public boolean verify(JComponent input) {
            try {
                return Integer.parseInt(((JTextArea)input).getText()) > 0;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }

    private JTable table;
    private Algorithm algorithm = Algorithm.NONE;

    private JTextArea textLambda;
    private JTextArea textMu;
    private JTextArea textN;
    private JTextArea textM;
    private JTextArea textC1;
    private JTextArea textC2;

    private JTextArea textCockroachesCount;
    private JTextArea textCockroachIterations;
    private JTextArea textCockroachDisperseStepSize;
    private JTextArea textCockroachSwarmStepSize;
    private JButton btnRunCockroaches;

    private JTextArea textCuckooNestsCount;
    private JTextArea textCuckooIterations;
    private JTextArea textCuckooAbandonProbability;
    private JTextArea textCuckooRandomStepSize;
    private JButton btnRunCuckoo;
    
    private JTextArea textHybridCount;
    private JTextArea textHybridIterations;
    private JTextArea textHybridDisperseStepSize;
    private JTextArea textHybridSwarmStepSize;
    private JTextArea textHybridAbandonProbability;
    private JTextArea textHybridFatality;
    private JButton btnRunHybrid;

    public Frame() {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private QueueCostFunction getCostFunction() {
        double lambda = Double.parseDouble(textLambda.getText());
        double mu = Double.parseDouble(textMu.getText());
        int N = Integer.parseInt(textN.getText());
        int m = Integer.parseInt(textM.getText());
        double c1 = Double.parseDouble(textC1.getText());
        double c2 = Double.parseDouble(textC2.getText());

        return new QueueCostFunction(m, N, lambda, mu, c1, c2);
    }

    private void runCockroach() {
        Cockroaches cockroaches = new Cockroaches();
        QueueCostFunction costFunction = getCostFunction();

        double disperseStepSize = Double.parseDouble(textCockroachDisperseStepSize.getText());
        double swarmStepSize = Double.parseDouble(textCockroachSwarmStepSize.getText());
        int cockroachesCount = Integer.parseInt(textCockroachesCount.getText());
        int iterations = Integer.parseInt(textCockroachIterations.getText());

        cockroaches.setCostFunction(costFunction);
        cockroaches.setDisperseStepSize(disperseStepSize);
        cockroaches.setSwarmStepSize(swarmStepSize);
        cockroaches.setN(costFunction.getN());

        int solution = cockroaches.solve(getTableModel(), cockroachesCount, iterations);
        System.out.printf("solution is: %d\n", solution);
    }

    private void runCuckoo() {
        QueueCostFunction costFunction = getCostFunction();

        int nestsCount = Integer.parseInt(textCuckooNestsCount.getText());
        int iterations = Integer.parseInt(textCockroachIterations.getText());
        double abandonProbability = Double.parseDouble(textCuckooAbandonProbability.getText());
        double randomStepSize = Double.parseDouble(textCuckooRandomStepSize.getText());

        try {
            System.out.println("Cuckoo Search solutions:");
            queuesys.search.cuckoo.CuckooSearch.optymalization(getTableModel(), nestsCount, iterations, abandonProbability, randomStepSize, costFunction.getN(), costFunction);
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void runHybrid() {
    	Hybrid hybrids = new Hybrid();
        QueueCostFunction costFunction = getCostFunction();

        double disperseStepSize = Double.parseDouble(textHybridDisperseStepSize.getText());
        double swarmStepSize = Double.parseDouble(textHybridSwarmStepSize.getText());
        int hybridCount = Integer.parseInt(textHybridCount.getText());
        int iterations = Integer.parseInt(textHybridIterations.getText());
        double abandonProbability = Double.parseDouble(textHybridAbandonProbability.getText());
        double fatality = Double.parseDouble(textHybridFatality.getText());


        hybrids.setCostFunction(costFunction);
        hybrids.setDisperseStepSize(disperseStepSize);
        hybrids.setSwarmStepSize(swarmStepSize);
        hybrids.setN(costFunction.getN());
        hybrids.setPa(abandonProbability);

        int solution = hybrids.solve(getTableModel(), hybridCount, iterations);
        System.out.printf("solution is: %d\n", solution);
    }

    public void runSimulation() {
        do {
            synchronized (this) {
                while (algorithm == Algorithm.NONE) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    /* program zamkniety */
                    if (!isShowing()) {
                        return;
                    }
                }
            }

            btnRunCockroaches.setEnabled(false);
            btnRunCuckoo.setEnabled(false);
            btnRunHybrid.setEnabled(false);

            getTableModel().reset();

            switch (algorithm) {
                case COCKROACH:
                    runCockroach();
                    break;
                case CUCKOO:
                    runCuckoo();
                    break;
                case HYBRID:
                    runHybrid();
                    break;
                default:
                    break;
            }

            btnRunCockroaches.setEnabled(true);
            btnRunCuckoo.setEnabled(true);
            btnRunHybrid.setEnabled(true);

            algorithm = Algorithm.NONE;
        } while (isShowing());
    }

    private JTextArea addParameterInput(String labelText, String defaultValue, JComponent parent, int yIndex, InputVerifier verifier) {
        JLabel label = new JLabel(labelText);
        JTextArea textArea = new JTextArea();

        label.setHorizontalAlignment(JLabel.RIGHT);

        textArea.setInputVerifier(verifier);
        textArea.setText(defaultValue);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(1, 1, 1, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.gridy = yIndex;

        constraints.weightx = 0.0;
        constraints.gridx = 0;
        parent.add(label, constraints);

        constraints.weightx = 1.0;
        constraints.gridx = 1;
        parent.add(textArea, constraints);

        return textArea;
    }

    private JButton addButton(String labelText, JComponent parent, int yIndex) {
        JButton button = new JButton(labelText);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = yIndex;
        constraints.gridwidth = 2;
        constraints.weightx = 1.0;

        parent.add(button, constraints);

        return button;
    }

    private void addVerticalSpacer(JComponent parent, int yIndex) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = yIndex;
        constraints.gridwidth = 2;
        constraints.weighty = 1.0;

        parent.add(Box.createVerticalBox(), constraints);
    }

    private JPanel createCommonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        textLambda = addParameterInput("Lambda:", "19", panel, 0, new DoubleVerifier());
        textMu =     addParameterInput("Mu:",     "10", panel, 1, new DoubleVerifier());
        textN =      addParameterInput("N:",      "15", panel, 2, new PositiveIntVerifier());
        textM =      addParameterInput("m:",      "1",  panel, 3, new PositiveIntVerifier());
        textC1 =     addParameterInput("C1:",     "4",  panel, 4, new DoubleVerifier());
        textC2 =     addParameterInput("C2:",     "12", panel, 5, new DoubleVerifier());

        addVerticalSpacer(panel, 6);
        return panel;
    }

    private JPanel createCockroachPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        textCockroachDisperseStepSize = addParameterInput("Disperse step size:", "2",    panel, 0, new NonNegativeDoubleVerifier());
        textCockroachSwarmStepSize =    addParameterInput("Swarm step size:",    "3",    panel, 1, new NonNegativeDoubleVerifier());
        textCockroachesCount =          addParameterInput("Cockroaches count:",  "100",  panel, 2, new PositiveIntVerifier());
        textCockroachIterations =       addParameterInput("Iterations:",         "1000", panel, 3, new PositiveIntVerifier());

        btnRunCockroaches = addButton("Run", panel, 4);
        btnRunCockroaches.addMouseListener(this);

        addVerticalSpacer(panel, 5);
        return panel;
    }

    private JPanel createCuckooPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        textCuckooNestsCount =         addParameterInput("Nests count:",         "20",   panel, 0, new PositiveIntVerifier());
        textCuckooIterations =         addParameterInput("Iterations:",          "1000", panel, 1, new PositiveIntVerifier());
        textCuckooAbandonProbability = addParameterInput("Abandon probability:", "0.25", panel, 2, new ProbabilityVerifier());
        textCuckooRandomStepSize =     addParameterInput("Random step size:",    "0.1",  panel, 3, new NonNegativeDoubleVerifier());

        btnRunCuckoo = addButton("Run", panel, 4);
        btnRunCuckoo.addMouseListener(this);

        addVerticalSpacer(panel, 5);
        return panel;
    }

    private JPanel createHybridPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());


        textHybridDisperseStepSize = addParameterInput("Disperse step size:", "2",    panel, 0, new NonNegativeDoubleVerifier());
        textHybridSwarmStepSize =    addParameterInput("Swarm step size:",    "3",    panel, 1, new NonNegativeDoubleVerifier());
        textHybridCount =          addParameterInput("Cockroaches count:",  "100",  panel, 2, new PositiveIntVerifier());
        textHybridIterations =       addParameterInput("Iterations:",         "1000", panel, 3, new PositiveIntVerifier());
        textHybridAbandonProbability = addParameterInput("Abandon probability:", "0.25", panel, 4, new ProbabilityVerifier());
        textHybridFatality = addParameterInput("Fatality:", "0.15", panel, 5, new ProbabilityVerifier());

        
        btnRunHybrid = addButton("Run", panel, 6);
        btnRunHybrid.addMouseListener(this);

        addVerticalSpacer(panel, 7);
        return panel;
    }

    private void createAndShowGUI() {
        setTitle("BO");
        addWindowListener(this);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;

        table = new JTable();
        table.setModel(new MyTableModel());
        getContentPane().add(new JScrollPane(table), constraints);

        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new GridBagLayout());
        getContentPane().add(sidePanel, constraints);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Cockroach", createCockroachPanel());
        tabbedPane.addTab("Cuckoo", createCuckooPanel());
        tabbedPane.addTab("Hybrid", createHybridPanel());

        constraints.gridy = 0;
        sidePanel.add(createCommonPanel(), constraints);
        constraints.gridy = 1;
        sidePanel.add(tabbedPane, constraints);
        sidePanel.add(Box.createVerticalGlue());

        pack();
        setVisible(true);
    }

    public MyTableModel getTableModel() {
        return (MyTableModel)table.getModel();
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {
        JButton button = (JButton)e.getComponent();

        synchronized (this) {
            if (button == btnRunCockroaches) {
                algorithm = Algorithm.COCKROACH;
            } else if (button == btnRunCuckoo) {
                algorithm = Algorithm.CUCKOO;
            } else if (button == btnRunHybrid) {
                algorithm = Algorithm.HYBRID;
            } else {
                throw new RuntimeException("something went terribly wrong");
            }

            notify();
        }
    }

    @Override public void windowOpened(WindowEvent e) {}
    @Override public void windowClosed(WindowEvent e) {}
    @Override public void windowIconified(WindowEvent e) {}
    @Override public void windowDeiconified(WindowEvent e) {}
    @Override public void windowActivated(WindowEvent e) {}
    @Override public void windowDeactivated(WindowEvent e) {}

    @Override
    public void windowClosing(WindowEvent e) {
        /* obudz watek wykonujacy obliczenia, jesli czeka na wybor algorytmu */
        synchronized (this) {
            notifyAll();
        }
    }

}

public class Main {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        Frame frame = new Frame();
        frame.runSimulation();
    }
