// Em Tetromino.java

public enum Tetromino {

    I(new int[][][]{
            {{0, 0, 0, 0}, {1, 1, 1, 1}, {0, 0, 0, 0}, {0, 0, 0, 0}},
            {{0, 1, 0, 0}, {0, 1, 0, 0}, {0, 1, 0, 0}, {0, 1, 0, 0}}
    }),

    O(new int[][][]{
            {{1, 1}, {1, 1}}
    }),

    T(new int[][][]{
            {{0, 1, 0}, {1, 1, 1}, {0, 0, 0}},
            {{0, 1, 0}, {0, 1, 1}, {0, 1, 0}},
            {{0, 0, 0}, {1, 1, 1}, {0, 1, 0}},
            {{0, 1, 0}, {1, 1, 0}, {0, 1, 0}}
    }),

    S(new int[][][]{
            {{0, 1, 1}, {1, 1, 0}, {0, 0, 0}},
            {{0, 1, 0}, {0, 1, 1}, {0, 0, 1}}
    }),

    Z(new int[][][]{
            {{1, 1, 0}, {0, 1, 1}, {0, 0, 0}},
            {{0, 0, 1}, {0, 1, 1}, {0, 1, 0}}
    }),

    J(new int[][][]{
            {{1, 0, 0}, {1, 1, 1}, {0, 0, 0}},
            {{0, 1, 1}, {0, 1, 0}, {0, 1, 0}},
            {{0, 0, 0}, {1, 1, 1}, {0, 0, 1}},
            {{0, 1, 0}, {0, 1, 0}, {1, 1, 0}}
    }),

    L(new int[][][]{
            {{0, 0, 1}, {1, 1, 1}, {0, 0, 0}},
            {{0, 1, 0}, {0, 1, 0}, {0, 1, 1}},
            {{0, 0, 0}, {1, 1, 1}, {1, 0, 0}},
            {{1, 1, 0}, {0, 1, 0}, {0, 1, 0}}
    }); // <-- ESTE PONTO E VÍRGULA É ESSENCIAL!

    // --- Campos e Métodos ---

    private final int[][][] shapes;

    // Construtor do Enum
    Tetromino(int[][][] shapes) {
        this.shapes = shapes;
    }

    // Retorna a matriz da forma para uma dada rotação
    public int[][] getShape(int rotation) {
        return shapes[rotation % shapes.length];
    }

    // Retorna o número de rotações possíveis
    public int getNumRotations() {
        return shapes.length;
    }
}