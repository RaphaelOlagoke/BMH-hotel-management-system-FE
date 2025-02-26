package com.bmh.hotelmanagementsystem.dto.inventory;

import com.bmh.hotelmanagementsystem.BackendService.enums.StockItemCategory;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class RefreshInventoryPageData {

    private VBox currentlySelectedVBox;
    private Label currentlySelectedLabel;

    private StockItemCategory stockItemCategory;

    public VBox getCurrentlySelectedVBox() {
        return currentlySelectedVBox;
    }

    public void setCurrentlySelectedVBox(VBox currentlySelectedVBox) {
        this.currentlySelectedVBox = currentlySelectedVBox;
    }

    public Label getCurrentlySelectedLabel() {
        return currentlySelectedLabel;
    }

    public void setCurrentlySelectedLabel(Label currentlySelectedLabel) {
        this.currentlySelectedLabel = currentlySelectedLabel;
    }

    public StockItemCategory getStockItemCategory() {
        return stockItemCategory;
    }

    public void setStockItemCategory(StockItemCategory stockItemCategory) {
        this.stockItemCategory = stockItemCategory;
    }
}


