package Controller;

import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;


public class TooltipCell extends ListCell<String> {
    public TooltipCell() {
    }

    @Override
    protected void updateItem(String s, boolean b) {
        super.updateItem(s, b);
        if (s == null || b) {
            setText(null);
        } else {
            setText(s);
        }
    }
}
