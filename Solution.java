public class Solution {

    public static void quickSort(int[] lst) {
        /* Sorts an array in the ascending order in O(n log n) time */
        int n = lst.length;
        qSort(lst, 0, n - 1);
    }

    private static void qSort(int[] lst, int lo, int hi) {
        if (lo < hi) {
            int p = partition(lst, lo, hi);
            qSort(lst, lo, p - 1);
            qSort(lst, p + 1, hi);
        }
    }

    private static int partition(int[] lst, int lo, int hi) {
    /*
      Picks the last element hi as a pivot
      and returns the index of pivot value in the sorted array */
        int pivot = lst[hi];
        int i = lo;
        for (int j = lo; j < hi; ++j) {
            if (lst[j] < pivot) {
                int tmp = lst[i];
                lst[i] = lst[j];
                lst[j] = tmp;
                i++;
            }
        }
        int tmp = lst[i];
        lst[i] = lst[hi];
        lst[hi] = tmp;
        return i;
    }

    public static void main(String[] args){
        int[] test = {1, 5, 3, 2, 8, 7, 6, 4};
        Solution.quickSort(test);
    }
}