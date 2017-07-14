package study.heaven7.com.android_study.import_object_array;

/**
 * 从string 资源构造 Category 数组
 */
public class Category {
    private Type id;
    private/* @ColorRes*/ int color;
    private /*@StringRes*/ String name;

    public Type getId() {
        return id;
    }

    public void setId(Type id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", color=" + color +
                ", name='" + name + '\'' +
                '}';
    }

    public enum Type{
        REGISTRATION,
        TO_ACCEPT,
        TO_COMPLETE,
        TO_VERIFY,
        CLOSED
    }
}