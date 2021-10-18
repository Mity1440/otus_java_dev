package homework;


import java.util.AbstractMap;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class CustomerService {

    private final TreeMap<Customer, String> map = new TreeMap<>(Comparator.comparingLong(Customer::getScores));

    public Map.Entry<Customer, String> getSmallest() {
        return getEntry(map.firstEntry());
    }

    public Map.Entry<Customer, String> getNext(Customer customer) {
        return getEntry(map.higherEntry(customer));
    }

    public void add(Customer customer, String data) {
        map.put(customer, data);
    }

    private Map.Entry<Customer, String> getEntry(Map.Entry<Customer, String> entry){

        Map.Entry<Customer, String> result = null;

        if (entry != null){
            result = new AbstractMap.SimpleImmutableEntry<>(getKeyCopyFromEntry(entry), entry.getValue());
        }

        return result;
    }

    private Customer getKeyCopyFromEntry(Map.Entry<Customer, String> entry) {

        Customer keyCopy = null;

        Customer key = entry.getKey();

        if (key != null){
            try {
                keyCopy = key.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

        return keyCopy;

    }


}
