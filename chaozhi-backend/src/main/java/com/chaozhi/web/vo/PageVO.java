package com.chaozhi.web.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageVO<T> implements Iterable<T>, Serializable {
    private List<T> items = new ArrayList<>();
    private long count;
    private int page;
    private int pageSize;

    public int getPages() {
        return getPageSize() <= 0 ? 1 : (int) Math.ceil((double) count / (double) getPageSize());
    }

    public boolean hasPrevious() { return getPage() > 1; }
    public boolean hasNext() { return getPage() < getPages(); }
    public boolean isFirst() { return !hasPrevious(); }
    public boolean isLast() { return !hasNext(); }
    public boolean hasItems() { return items.size() > 0; }

    public Iterator<T> iterator() { return getItems().iterator(); }
}