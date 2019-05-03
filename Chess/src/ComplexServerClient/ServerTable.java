package ComplexServerClient;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class ServerTable {

    private static final int NUMBER_OF_COLUMNS = 2;
    private static final int LEFT_COLUMN = 0;
    private static final int RIGHT_COLUMN = 1;

    private static final String[] TABLE_TITLE = {"White Moves", "Black Moves"};
    
    private List<Client> pendingPlayers;
    
    private final JTable history = new JTable();
    private final JScrollPane scrollPane = new JScrollPane();
    private final DefaultTableCellRenderer tableCellRenderer = new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            JComponent expand = (JComponent) super.getTableCellRendererComponent(table,
                    value, isSelected, hasFocus, row, column);
            expand.setToolTipText(value == null ? "" : value.toString());
            return expand;
        }
    };
    private final DefaultTableModel tableModel = new DefaultTableModel(null, TABLE_TITLE) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public ServerTable(int x, int y, int width, int height) {
        scrollPane.setViewportView(history);

        history.setRowSelectionAllowed(false);
        history.setColumnSelectionAllowed(false);
        history.setCellSelectionEnabled(true);

        tableModel.setColumnCount(NUMBER_OF_COLUMNS);
        history.setModel(tableModel);
        history.setDefaultRenderer(Object.class, tableCellRenderer);

        JTableHeader header = history.getTableHeader();
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);
        //final Color originalBackground = history.getSelectionBackground();
        //history.setSelectionBackground(Color.YELLOW);
        history.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent me) {

            }

            @Override
            public void mousePressed(MouseEvent me) {
                //history.setSelectionBackground(Color.YELLOW);
                history.setCellSelectionEnabled(true);
            }

            @Override
            public void mouseReleased(MouseEvent me) {
                //history.setCellSelectionEnabled(false);
            }

            @Override
            public void mouseEntered(MouseEvent me) {
                //history.setCellSelectionEnabled(true);
            }

            @Override
            public void mouseExited(MouseEvent me) {
                //history.setSelectionBackground(originalBackground);
                history.setCellSelectionEnabled(false);
            }
        });

        scrollPane.setBounds(x, y, width, height);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        scrollPane.setToolTipText("Move History");
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public void setData(List<String> leftColumn, List<String> rightColumn) {
        int leftColumnSize = leftColumn.size();
        int rightColumnSize = rightColumn.size();
        tableModel.setRowCount(leftColumnSize > rightColumnSize ? leftColumnSize : rightColumnSize);
        for (int row = 0; row < leftColumnSize; ++row) {
            tableModel.setValueAt(null, row, LEFT_COLUMN);
            tableModel.setValueAt(leftColumn.get(row), row, LEFT_COLUMN);
        }
        for (int row = 0; row < rightColumnSize; ++row) {
            tableModel.setValueAt(null, row, RIGHT_COLUMN);
            tableModel.setValueAt(rightColumn.get(row), row, RIGHT_COLUMN);
        }
    }
}