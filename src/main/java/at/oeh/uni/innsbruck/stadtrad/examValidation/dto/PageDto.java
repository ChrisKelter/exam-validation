package at.oeh.uni.innsbruck.stadtrad.examValidation.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public class PageDto<T> {
    private int page;
    private int size;
    private long numberOfElements;
    private List<T> content;
    private boolean hasContent;
    private boolean isFirst;
    private boolean isLast;
    private boolean hasNext;
    private boolean hasPrevious;

    public static <S, T> PageDto<T> fromPaged(Page<S> page) {
        PageDto<T> pageDto = new PageDto<>();
        pageDto.setPage(page.getNumber());
        pageDto.setSize(page.getSize());
        pageDto.setNumberOfElements(page.getTotalElements());
        pageDto.setFirst(page.isFirst());
        pageDto.setLast(page.isLast());
        pageDto.setHasContent(page.hasContent());
        pageDto.setHasPrevious(page.hasPrevious());
        return pageDto;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getNumberOfElements() {
        return numberOfElements;
    }

    public void setNumberOfElements(long numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public boolean isHasContent() {
        return hasContent;
    }

    public void setHasContent(boolean hasContent) {
        this.hasContent = hasContent;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }

    public boolean isLast() {
        return isLast;
    }

    public void setLast(boolean last) {
        isLast = last;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public boolean isHasPrevious() {
        return hasPrevious;
    }

    public void setHasPrevious(boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }
}
