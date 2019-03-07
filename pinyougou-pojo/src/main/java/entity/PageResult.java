package entity;

import java.io.Serializable;
import java.util.List;

public class PageResult<E> implements Serializable {
    private Long total;
    private List<E> rows;

    public PageResult(Long total, List<E> list) {
        this.total = total;
        this.rows = list;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<E> getRows() {
        return rows;
    }

    public void setRows(List<E> rows) {
        this.rows = rows;
    }
}
