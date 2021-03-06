// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.teacher.table;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * @author Sam Reid
 */
public class Table extends JPanel {
    public JTable table;

    public Table() {
        setLayout( new BorderLayout() );
        final ArrayList<StudentInfo> rows = new ArrayList<StudentInfo>();
        rows.add( new StudentInfo( "Hector-123", "alpha", new Date(), 1000, true, "Gravity and Orbits", 23, 78, true, 100E24 ) );
        rows.add( new StudentInfo( "Hector-123", "beta", new Date(), 200, true, "Gravity and Orbits", 22, 79, true, 95E24 ) );
        rows.add( new StudentInfo( "Hector-123", "17", new Date(), 3000, false, "Gravity and Orbits", 30, 80, false, 98E23 ) );
        table = new JTable( new AbstractTableModel() {
            public String[] columns = new String[] { "Class ID", "User ID", "Start Time", "Uptime (minutes)", "Online", "Sim", "Latency (ms)", "Session ID", "Earth Crashed", "Sun Mass (kg)" };

            public int getRowCount() {
                return rows.size();
            }

            public int getColumnCount() {
                return columns.length;
            }

            public Object getValueAt( int rowIndex, int columnIndex ) {
                return rows.get( rowIndex ).getValue( columnIndex );
            }

            @Override public String getColumnName( int column ) {
                return columns[column];
            }

            {
            }
        } ) {{
            setFillsViewportHeight( true );
            calcColumnWidths( this );
        }};
        add( new JScrollPane( table ), BorderLayout.CENTER );
    }

    //http://www.javakb.com/Uwe/Forum.aspx/java-programmer/28790/JTable-and-optimal-column-width
    public void calcColumnWidths( JTable table ) {
        JTableHeader header = table.getTableHeader();
        TableCellRenderer defaultHeaderRenderer = null;

        if ( header != null ) { defaultHeaderRenderer = header.getDefaultRenderer(); }

        TableColumnModel columns = table.getColumnModel();
        TableModel data = table.getModel();
        int margin = columns.getColumnMargin(); // only JDK1.3
        int rowCount = data.getRowCount();
        int totalWidth = 0;

        for ( int i = columns.getColumnCount() - 1; i >= 0; --i ) {
            TableColumn column = columns.getColumn( i );
            int columnIndex = column.getModelIndex();
            int width = -1;

            TableCellRenderer h = column.getHeaderRenderer();

            if ( h == null ) { h = defaultHeaderRenderer; }

            if ( h != null ) // Not explicitly impossible
            {
                Component c = h.getTableCellRendererComponent( table, column
                        .getHeaderValue(), false, false, -1, i );
                width = c.getPreferredSize().width;
            }

            for ( int row = rowCount - 1; row >= 0; --row ) {
                TableCellRenderer r = table.getCellRenderer( row, i );
                Component c = r.getTableCellRendererComponent( table, data.getValueAt( row, columnIndex ), false, false,
                                                               row, i );
                width = Math.max( width, c.getPreferredSize().width );
            }

            if ( width >= 0 ) {
                column.setPreferredWidth( width + margin ); // <1.3:
            }
            else { totalWidth += column.getPreferredWidth(); }
        }
    }

    public static void main( String[] args ) {
        new JFrame() {{
            final Table contentPane = new Table();
            setContentPane( contentPane );
            setDefaultCloseOperation( EXIT_ON_CLOSE );
            setSize( 1024, 768 );
            contentPane.calcColumnWidths( contentPane.table );
        }}.setVisible( true );
    }
}
