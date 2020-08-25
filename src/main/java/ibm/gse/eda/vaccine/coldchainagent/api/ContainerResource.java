package ibm.gse.eda.vaccine.coldchainagent.api;

import java.util.ArrayList;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Qualifier;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;

import ibm.gse.eda.vaccine.coldchainagent.api.dto.Returntype;
import ibm.gse.eda.vaccine.coldchainagent.domain.ContainerTracker;
import ibm.gse.eda.vaccine.coldchainagent.domain.TelemetryAssessor;

/**
 * A simple resource retrieving the last n telemetries read from the topic,
 * and the reefer with temperature raise
 */
@Path("/reefer-tracker")
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class ContainerResource {

    @Inject
    KafkaStreams streams;

    @GET
    @Path("/{reeferID}")
    public ContainerTracker getContainer(@PathParam("reeferID") final String reeferID) {
        final StoreQueryParameters<ReadOnlyKeyValueStore<String,ContainerTracker>> parameters = StoreQueryParameters.fromNameAndType(TelemetryAssessor.CONTAINER_TABLE,QueryableStoreTypes.keyValueStore());
        return streams.store(parameters).get(reeferID);
    }

    @GET
    public ArrayList<Returntype> getktable() {
        final StoreQueryParameters<ReadOnlyKeyValueStore<String,ContainerTracker>> parameters = StoreQueryParameters.fromNameAndType(TelemetryAssessor.CONTAINER_TABLE,QueryableStoreTypes.keyValueStore());
        final KeyValueIterator<String, ContainerTracker> val =  streams.store(parameters).all();
        final ArrayList<Returntype> returnList= new ArrayList<Returntype>();
        while (val.hasNext()){
            final KeyValue<String, ContainerTracker> keypair = val.next();
            returnList.add(new Returntype(keypair.key, keypair.value));
        }
        return returnList;
    }
}

