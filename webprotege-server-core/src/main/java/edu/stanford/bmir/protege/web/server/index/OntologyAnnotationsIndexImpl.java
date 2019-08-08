package edu.stanford.bmir.protege.web.server.index;

import edu.stanford.bmir.protege.web.shared.inject.ProjectSingleton;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.UnknownOWLOntologyException;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-08-06
 */
@ProjectSingleton
public class OntologyAnnotationsIndexImpl implements OntologyAnnotationsIndex {

    @Nonnull
    private final OntologyIndex ontologyIndex;

    @Inject
    public OntologyAnnotationsIndexImpl(@Nonnull OntologyIndex ontologyIndex) {
        this.ontologyIndex = checkNotNull(ontologyIndex);
    }

    @Nonnull
    @Override
    public Stream<OWLAnnotation> getOntologyAnnotations(@Nonnull OWLOntologyID ontologyID) {
        return ontologyIndex.getOntology(ontologyID)
                .map(ont -> ont.getAnnotations().stream())
                .orElseThrow(() -> new UnknownOWLOntologyException(ontologyID));
    }
}
