package ui.components;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.Question;

/** Feeds a list of questions to a JTable. The table only displays; it never changes the questions. */
public class QuestionTableModel extends AbstractTableModel {

    private static final String[] COLUMNS = {"ID", "Question", "Type", "Category", "Level"};

    private List<Question> questions = new ArrayList<>();

    public void setQuestions(List<Question> questions) {
        this.questions = new ArrayList<>(questions);
        fireTableDataChanged();
    }

    public Question getQuestionAt(int row) {
        return questions.get(row);
    }

    @Override
    public int getRowCount() {
        return questions.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Object getValueAt(int row, int column) {
        Question q = questions.get(row);
        switch (column) {
            case 0: return q.getId();
            case 1: return q.getText();
            case 2: return q.getTypeName();
            case 3: return q.getCategory();
            default: return q.getDifficulty().getLabel();
        }
    }
}
