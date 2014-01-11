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

import javax.swing.*;

class Frame extends JFrame implements MouseListener, WindowListener {
    static enum Algorithm {
        NONE, COCKROACH, CUCKOO, HYBRID
    }

    static abstract class VerifierBase extends InputVerifier {
        Component[] componentsToLock;

        protected VerifierBase(Component[] componentsToLock) {
            this.componentsToLock = componentsToLock;
        }

        protected void unlockComponents() {
            for (Component c: componentsToLock) {
                c.setEnabled(true);
            }
        }

        protected void lockComponents() {
            for (Component c: componentsToLock) {
                c.setEnabled(false);
            }
        }
    }

    /* dowolna liczba zmiennoprzecinkowa */
    static class DoubleVerifier extends VerifierBase {
        public DoubleVerifier(Component[] componentsToLock) {
            super(componentsToLock);
        }

        @Override
        public boolean verify(JComponent input) {
            try {
                Double.parseDouble(((JTextField)input).getText());
                unlockComponents();
                return true;
            } catch (NumberFormatException e) {
                lockComponents();
                return false;
            }
        }
    }

    /* dodatnia liczba zmiennoprzecinkowa */
    static class PositiveDoubleVerifier extends VerifierBase {
        public PositiveDoubleVerifier(Component[] componentsToLock) {
            super(componentsToLock);
        }

        @Override
        public boolean verify(JComponent input) {
            try {
                if (Double.parseDouble(((JTextField)input).getText()) > 0.0) {
                    unlockComponents();
                    return true;
                }
            } catch (NumberFormatException e) {
            }

            lockComponents();
            return false;
        }
    }

    /* nieujemna liczba zmiennoprzecinkowa (x >= 0) */
    static class NonNegativeDoubleVerifier extends VerifierBase {
        public NonNegativeDoubleVerifier(Component[] componentsToLock) {
            super(componentsToLock);
        }

        @Override
        public boolean verify(JComponent input) {
            try {
                if (Double.parseDouble(((JTextField)input).getText()) > 0.0) {
                    unlockComponents();
                    return true;
                }
            } catch (NumberFormatException e) {
            }

            lockComponents();
            return false;
        }
    }

    /* prawdopodobienstwo (0 <= x <= 1) */
    static class ProbabilityVerifier extends VerifierBase {
        public ProbabilityVerifier(Component[] componentsToLock) {
            super(componentsToLock);
        }

        @Override
        public boolean verify(JComponent input) {
            try {
                double val = Double.parseDouble(((JTextField)input).getText());
                if (val >= 0.0 && val <= 1.0) {
                    unlockComponents();
                    return true;
                }
            } catch (NumberFormatException e) {
            }

            lockComponents();
            return false;
        }
    }

    /* dodatnia liczba calkowita (x > 0) */
    static class PositiveIntVerifier extends VerifierBase {
        public PositiveIntVerifier(Component[] componentsToLock) {
            super(componentsToLock);
        }

        @Override
        public boolean verify(JComponent input) {
            try {
                if (Integer.parseInt(((JTextField)input).getText()) > 0) {
                    unlockComponents();
                    return true;
                }
            } catch (NumberFormatException e) {
            }

            lockComponents();
            return false;
        }
    }

    private JTable table;
    private Algorithm algorithm = Algorithm.NONE;
    private boolean isClosing = false;

    private JTextField textLambda;
    private JTextField textMu;
    private JTextField textN;
    private JTextField textM;
    private JTextField textC1;
    private JTextField textC2;

    private JTextField textCockroachesCount;
    private JTextField textCockroachIterations;
    private JTextField textCockroachDisperseStepSize;
    private JTextField textCockroachSwarmStepSize;
    private JButton btnRunCockroaches;

    private JTextField textCuckooNestsCount;
    private JTextField textCuckooIterations;
    private JTextField textCuckooAbandonProbability;
    private JTextField textCuckooRandomStepSize;
    private JButton btnRunCuckoo;

    private JTextField textHybridCount;
    private JTextField textHybridIterations;
    private JTextField textHybridDisperseStepSize;
    private JTextField textHybridSwarmStepSize;
    private JTextField textHybridAbandonProbability;
    private JTextField textHybridFatality;
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

    private void runCockroach(QueueCostFunction costFunction) {
        Cockroaches cockroaches = new Cockroaches();

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

    private void runCuckoo(QueueCostFunction costFunction) {
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

    private void runHybrid(QueueCostFunction costFunction) {
    	Hybrid hybrids = new Hybrid();

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
                    if (isClosing) {
                        return;
                    }
                }
            }

            btnRunCockroaches.setEnabled(false);
            btnRunCuckoo.setEnabled(false);
            btnRunHybrid.setEnabled(false);

            QueueCostFunction costFunction = getCostFunction();
            getTableModel().reset(costFunction);

            switch (algorithm) {
                case COCKROACH:
                    runCockroach(costFunction);
                    break;
                case CUCKOO:
                    runCuckoo(costFunction);
                    break;
                case HYBRID:
                    runHybrid(costFunction);
                    break;
                default:
                    break;
            }

            btnRunCockroaches.setEnabled(true);
            btnRunCuckoo.setEnabled(true);
            btnRunHybrid.setEnabled(true);

            algorithm = Algorithm.NONE;
        } while (!isClosing);
    }

    private JTextField addParameterInput(String labelText, String defaultValue, JComponent parent, int yIndex, InputVerifier verifier) {
        JLabel label = new JLabel(labelText);
        JTextField textField = new JTextField();

        label.setHorizontalAlignment(JLabel.RIGHT);

        textField.setInputVerifier(verifier);
        textField.setText(defaultValue);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(1, 1, 1, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.gridy = yIndex;

        constraints.weightx = 0.0;
        constraints.gridx = 0;
        parent.add(label, constraints);

        constraints.weightx = 1.0;
        constraints.gridx = 1;
        parent.add(textField, constraints);

        return textField;
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

        Component[] buttonsToLock = new Component[] {
                btnRunCockroaches, btnRunCuckoo, btnRunHybrid
        };

        textLambda = addParameterInput("Lambda:", "19", panel, 0, new DoubleVerifier(buttonsToLock));
        textMu =     addParameterInput("Mu:",     "10", panel, 1, new PositiveDoubleVerifier(buttonsToLock));
        textN =      addParameterInput("N:",      "15", panel, 2, new PositiveIntVerifier(buttonsToLock));
        textM =      addParameterInput("m:",      "1",  panel, 3, new PositiveIntVerifier(buttonsToLock));
        textC1 =     addParameterInput("C1:",     "4",  panel, 4, new DoubleVerifier(buttonsToLock));
        textC2 =     addParameterInput("C2:",     "12", panel, 5, new DoubleVerifier(buttonsToLock));

        addVerticalSpacer(panel, 6);
        return panel;
    }

    private JPanel createCockroachPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        btnRunCockroaches = addButton("Run", panel, 4);
        btnRunCockroaches.addMouseListener(this);

        Component[] buttonsToLock = new Component[] { btnRunCockroaches };

        textCockroachDisperseStepSize = addParameterInput("Disperse step size:", "4",  panel, 0, new NonNegativeDoubleVerifier(buttonsToLock));
        textCockroachSwarmStepSize =    addParameterInput("Swarm step size:",    "5",  panel, 1, new NonNegativeDoubleVerifier(buttonsToLock));
        textCockroachesCount =          addParameterInput("Cockroaches count:",  "5",  panel, 2, new PositiveIntVerifier(buttonsToLock));
        textCockroachIterations =       addParameterInput("Iterations:",         "50", panel, 3, new PositiveIntVerifier(buttonsToLock));

        addVerticalSpacer(panel, 5);
        return panel;
    }

    private JPanel createCuckooPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        btnRunCuckoo = addButton("Run", panel, 4);
        btnRunCuckoo.addMouseListener(this);

        Component[] buttonsToLock = new Component[] { btnRunCuckoo };

        textCuckooNestsCount =         addParameterInput("Nests count:",         "20",   panel, 0, new PositiveIntVerifier(buttonsToLock));
        textCuckooIterations =         addParameterInput("Iterations:",          "50",   panel, 1, new PositiveIntVerifier(buttonsToLock));
        textCuckooAbandonProbability = addParameterInput("Abandon probability:", "0.25", panel, 2, new ProbabilityVerifier(buttonsToLock));
        textCuckooRandomStepSize =     addParameterInput("Random step size:",    "0.1",  panel, 3, new NonNegativeDoubleVerifier(buttonsToLock));

        addVerticalSpacer(panel, 5);
        return panel;
    }

    private JPanel createHybridPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        btnRunHybrid = addButton("Run", panel, 6);
        btnRunHybrid.addMouseListener(this);

        Component[] buttonsToLock = new Component[] { btnRunHybrid };

        textHybridDisperseStepSize =   addParameterInput("Disperse step size:",  "2",    panel, 0, new NonNegativeDoubleVerifier(buttonsToLock));
        textHybridSwarmStepSize =      addParameterInput("Swarm step size:",     "3",    panel, 1, new NonNegativeDoubleVerifier(buttonsToLock));
        textHybridCount =              addParameterInput("Cockroaches count:",   "5",    panel, 2, new PositiveIntVerifier(buttonsToLock));
        textHybridIterations =         addParameterInput("Iterations:",          "50",   panel, 3, new PositiveIntVerifier(buttonsToLock));
        textHybridAbandonProbability = addParameterInput("Abandon probability:", "0.25", panel, 4, new ProbabilityVerifier(buttonsToLock));
        textHybridFatality =           addParameterInput("Fatality:",            "0.15", panel, 5, new ProbabilityVerifier(buttonsToLock));

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

        if (!button.hasFocus()) {
            return;
        }

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
        isClosing = true;
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
}
