package ch.isbsib.sparql.bed;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.broad.tribble.Feature;
import org.broad.tribble.annotation.Strand;
import org.broad.tribble.bed.BEDFeature;
import org.broad.tribble.bed.FullBEDFeature.Exon;
import org.openrdf.model.IRI;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;

public class BEDToTripleConverter {
	private final boolean rdftype;
	private final boolean faldobegin;
	private final boolean faldoend;

	public BEDToTripleConverter(ValueFactory vf, IRI... preds) {
		super();
		this.vf = vf;
		List<IRI> predList = Arrays.asList(preds);
		boolean tempType = predList.contains(RDF.TYPE);
		boolean tempfaldobegin = predList.contains(FALDO.BEGIN_PREDICATE);
		boolean tempfaldoend = predList.contains(FALDO.END_PREDICATE);

		if (predList.isEmpty() || predList.contains(null)) {
			tempType = true;
			tempfaldobegin = true;
			tempfaldoend = true;
		}
		rdftype = tempType;
		faldobegin = tempfaldobegin;
		faldoend = tempfaldoend;
		// type = predList.contains(RDF.TYPE) || predList.isEmpty() ||
		// predList.contains(null);
	}

	private final ValueFactory vf;

	public List<Statement> convertLineToTriples(String filePath,
			Feature feature, long lineNo) {
		List<Statement> stats = new ArrayList<Statement>(28);
		String recordPath = filePath + '/' + lineNo;
		IRI recordId = vf.createIRI(recordPath);
		IRI alignStartId = vf.createIRI(recordPath + "#start");
		IRI alignEndId = vf.createIRI(recordPath + "#end");

		add(stats, recordId, BED.CHROMOSOME, feature.getChr());

		if (rdftype) {
			rdfTypesForFeature(stats, recordId, alignStartId, alignEndId);
		}
		if (faldobegin) {
			add(stats, recordId, FALDO.BEGIN_PREDICATE, alignStartId);
		}
		add(stats, alignStartId, FALDO.POSTION_PREDICATE, feature.getStart());
		add(stats, alignStartId, FALDO.REFERENCE_PREDICATE, feature.getChr());

		if (faldoend) {
			add(stats, recordId, FALDO.END_PREDICATE, alignEndId);
		}
		add(stats, alignEndId, FALDO.POSTION_PREDICATE, feature.getEnd());
		add(stats, alignEndId, FALDO.REFERENCE_PREDICATE, feature.getChr());
		if (feature instanceof BEDFeature) {
			stats.addAll(convertBEDFeatureToTriples(filePath, (BEDFeature) feature,
					lineNo));
		}
		return stats;
	}

	protected void rdfTypesForFeature(List<Statement> stats, IRI recordId,
			IRI alignStartId, IRI alignEndId) {
		add(stats, recordId, RDF.TYPE, BED.FEATURE_CLASS);
		add(stats, recordId, RDF.TYPE, FALDO.REGION_CLASS);
		add(stats, alignStartId, RDF.TYPE, FALDO.EXACT_POSITION_CLASS);
		add(stats, alignEndId, RDF.TYPE, FALDO.EXACT_POSITION_CLASS);
	}

	private List<Statement> convertBEDFeatureToTriples(String filePath,
			BEDFeature feature, long lineNo) {
		List<Statement> stats = new ArrayList<Statement>(28);
		String recordPath = filePath + '/' + lineNo;
		IRI recordId = vf.createIRI(recordPath);
		if (feature.getName() != null) // name
			add(stats, recordId, RDFS.LABEL, feature.getName());
		if (feature.getScore() != Float.NaN) // score
			add(stats, recordId, BED.SCORE, feature.getScore());
		if (rdftype)
			addStrandedNessInformation(stats, feature, recordId);
		// we skip position 6,7 and 8 as these are colouring instructions

		for (Exon exon : feature.getExons()) {
			convertExon(feature, stats, recordPath, recordId, exon);
		}
		return stats;
	}

	protected void convertExon(BEDFeature feature, List<Statement> stats,
			String recordPath, IRI recordId, Exon exon) {
		String exonPath = recordPath + "/exon/" + exon.getNumber();
		IRI exonId = vf.createIRI(exonPath);
		IRI beginId = vf.createIRI(exonPath + "/begin");
		IRI endId = vf.createIRI(exonPath + "/end");
		add(stats, recordId, BED.EXON, endId);
		if (rdftype) {
			add(stats, exonId, RDF.TYPE, FALDO.REGION_CLASS);
			add(stats, endId, RDF.TYPE, FALDO.EXACT_POSITION_CLASS);
		}
		if (faldobegin) {
			add(stats, exonId, FALDO.BEGIN_PREDICATE, beginId);
		}
		add(stats, beginId, RDF.TYPE, FALDO.EXACT_POSITION_CLASS);
		add(stats, beginId, FALDO.POSTION_PREDICATE, exon.getCdStart());
		add(stats, beginId, FALDO.REFERENCE_PREDICATE, feature.getChr());
		if (faldoend) {
			add(stats, exonId, FALDO.END_PREDICATE, endId);
		}
		add(stats, endId, FALDO.POSTION_PREDICATE, exon.getCdEnd());
		add(stats, endId, FALDO.REFERENCE_PREDICATE, feature.getChr());
	}

	protected void addStrandedNessInformation(List<Statement> statements,
			BEDFeature feature, IRI alignEndId) {

		if (Strand.POSITIVE == feature.getStrand()) {
			add(statements, alignEndId, RDF.TYPE,
					FALDO.FORWARD_STRAND_POSITION_CLASS);
		} else if (Strand.NEGATIVE == feature.getStrand()) {
			add(statements, alignEndId, RDF.TYPE,
					FALDO.REVERSE_STRANDED_POSITION_CLASS);
		} else {
			add(statements, alignEndId, RDF.TYPE, FALDO.STRANDED_POSITION_CLASS);
		}

	}

	private void add(List<Statement> statements, IRI subject, IRI predicate,
			String string) {
		add(statements, subject, predicate, vf.createLiteral(string));

	}

	private void add(List<Statement> statements, IRI subject, IRI predicate,
			int string) {
		add(statements, subject, predicate, vf.createLiteral(string));

	}

	private void add(List<Statement> statements, IRI subject, IRI predicate,
			float string) {
		add(statements, subject, predicate, vf.createLiteral(string));

	}

	private void add(List<Statement> statements, Resource subject,
			IRI predicate, Value object) {
		statements.add(vf.createStatement(subject, predicate, object));
	}
}
