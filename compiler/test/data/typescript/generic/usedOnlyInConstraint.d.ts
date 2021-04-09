declare interface A {
    forEach<T, U extends ArrayLike<T>>(
        obj: U,
        iterator: (value: U[number], key: number, obj: U) => void,
        context?: any
    ): U;
}