package nl.pouwels.nes.ppu;

public class ObjectAttributeEntry {
    public int y_8;
    public int id_8;
    public int attribute;
    public int x_8;

    public int get(int index) {
        switch (index) {
            case 0:
                return y_8;
            case 1:
                return id_8;
            case 2:
                return attribute;
            case 3:
                return x_8;
        }
        throw new RuntimeException("Error getting object attribute entry, got index: " + index);
    }

    public void set(int index, int data_8) {
        switch (index) {
            case 0:
                y_8 = data_8;
                break;
            case 1:
                id_8 = data_8;
                break;
            case 2:
                attribute = data_8;
                break;
            case 3:
                x_8 = data_8;
                break;
            default:
                throw new RuntimeException("Error getting object attribute entry, got index: " + index);
        }
    }
}
