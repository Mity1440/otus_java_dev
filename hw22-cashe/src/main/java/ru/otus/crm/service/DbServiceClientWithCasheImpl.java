package ru.otus.crm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.cachehw.HwCache;
import ru.otus.core.repository.DataTemplate;
import ru.otus.core.sessionmanager.TransactionManager;
import ru.otus.crm.model.Client;

import java.util.Optional;

public class DbServiceClientWithCasheImpl extends DbServiceClientImpl{

    private static final Logger log = LoggerFactory.getLogger(DbServiceClientWithCasheImpl.class);

    private final HwCache<Long, Client> cashe;

    public DbServiceClientWithCasheImpl(TransactionManager transactionManager,
                                        DataTemplate<Client> clientDataTemplate,
                                        HwCache<Long, Client> cashe) {
        super(transactionManager, clientDataTemplate);
        this.cashe = cashe;

        if (cashe == null){
            throw new RuntimeException("Creating object error");
        }

    }

    @Override
    public Client saveClient(Client client) {
        return transactionManager.doInTransaction(session -> {
            var clientCloned = client.clone();
            if (client.getId() == null) {
                clientDataTemplate.insert(session, clientCloned);
                log.info("created client: {}", clientCloned);
            } else {
                clientDataTemplate.update(session, clientCloned);
                log.info("updated client: {}", clientCloned);
            }
            cashe.put(clientCloned.getId(), clientCloned);
            return clientCloned;
        });
    }

    @Override
    public Optional<Client> getClient(long id) {
        return transactionManager.doInReadOnlyTransaction(session -> {
            var clientOptional = Optional.ofNullable(cashe.get(id));
            if (!clientOptional.isPresent()){
                clientOptional = clientDataTemplate.findById(session, id);
            }
            log.info("client: {}", clientOptional);
            return clientOptional;
        });
    }

}
