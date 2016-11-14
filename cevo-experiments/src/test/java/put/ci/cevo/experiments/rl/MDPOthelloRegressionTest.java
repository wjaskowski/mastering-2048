package put.ci.cevo.experiments.rl;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import put.ci.cevo.experiments.othello.OthelloInteraction;
import put.ci.cevo.experiments.othello.OthelloWPCInteraction;
import put.ci.cevo.experiments.wpc.WPCIndividualFactory;
import put.ci.cevo.experiments.wpc.othello.mappers.WPCOthelloPlayerMapper;
import put.ci.cevo.framework.factories.StaticPopulationFactory;
import put.ci.cevo.framework.factories.UniformRandomPopulationFactory;
import put.ci.cevo.framework.interactions.InteractionResult;
import put.ci.cevo.framework.measures.AgainstPlayerPerformanceMeasure;
import put.ci.cevo.framework.measures.ExpectedUtility;
import put.ci.cevo.framework.measures.PerformanceMeasure;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.games.PointPerPieceGameResultEvaluator;
import put.ci.cevo.games.board.Board;
import put.ci.cevo.games.othello.OthelloBoard;
import put.ci.cevo.games.othello.mdp.GameOpponentEnvironment;
import put.ci.cevo.games.othello.mdp.OthelloSelfPlayEnvironment;
import put.ci.cevo.games.othello.mdp.OthelloMove;
import put.ci.cevo.games.othello.mdp.OthelloState;
import put.ci.cevo.games.othello.players.published.OthelloStandardWPCHeuristic;
import put.ci.cevo.rl.agent.Agent;
import put.ci.cevo.rl.agent.functions.RealFunction;
import put.ci.cevo.rl.agent.functions.wpc.WPC;
import put.ci.cevo.rl.environment.Environment;
import put.ci.cevo.rl.environment.RandomizedEnvironment;

import java.util.List;

public class MDPOthelloRegressionTest {

	private OthelloWPCInteraction othello, othello2;
	private ThreadedContext context;
	private MDPGenotypeMappingInteraction<OthelloState, OthelloMove, RealFunction, WPC> mdpInteraction,
			mdpInteraction2;

	@Before
	public void setUp() {
		Environment<OthelloState, OthelloMove> env = new OthelloSelfPlayEnvironment();
		RealFunctionGameAgentMapping<OthelloState, OthelloMove> agentMapping = new RealFunctionGameAgentMapping<>(env);
		MDPEpisodeInteraction<OthelloState, OthelloMove> interaction = new MDPEpisodeInteraction<>(
			new OthelloPointPerPieceInteractionEvaluator());
		MDPEpisodeInteraction<OthelloState, OthelloMove> interaction2 = new MDPEpisodeInteraction<>(
			new OthelloInteractionEvaluator());
		WPCOpponentEnvironmentMapping<OthelloState, OthelloMove> environmentMapping = new WPCOpponentEnvironmentMapping<>(
			env);

		mdpInteraction = new MDPGenotypeMappingInteraction<>(agentMapping, environmentMapping, interaction);
		mdpInteraction2 = new MDPGenotypeMappingInteraction<>(agentMapping, environmentMapping, interaction2);

		othello = new OthelloWPCInteraction(new PointPerPieceGameResultEvaluator(), false);
		othello2 = new OthelloWPCInteraction(true);
		context = new ThreadedContext(1234);
	}

	@Test
	public void testInteractRegression1() throws Exception {
		//@formatter:off
		WPC player1 = new WPC(new double[] { 0.13005979933401246,-0.5949268639190439,-0.5411731074588717,-0.6319496062237047,-0.6219616380453297,-0.23718487514938102,0.1023391301141996,-0.7353940980093898,-0.878285949202136,0.42285439222442367,0.06207788401981462,0.8091541584434383,-0.7720532090879453,-0.7986044710477849,-0.7735294515235709,-0.13087037744295893,0.8691160428648268,0.6727163739604132,0.25429302105239326,-0.459263310068621,-0.19363967712471197,0.9014935033076821,-0.867956033494695,0.6756377253511325,0.5450346520524252,0.7968602353689649,0.7165119714359238,0.27080001764299944,-0.7956618891457681,-0.5488747833356369,0.9785782112449439,0.29595799176258986,-0.13590777231989692,-0.6067172132441248,-0.844725335473901,0.02521555602751402,-0.6931474472266528,-0.13411441590000317,0.9082070486891154,0.13609082827185892,-0.012088185356578851,-0.14604968774169702,0.05413015299152013,0.24511563061123764,0.6394299713486844,-0.170199115343312,-0.05377824514494245,0.34920493167706157,0.2046989017799956,-0.291676926566653,-0.536921892129774,0.7731348146985684,0.6097407968359252,0.7345011535843016,0.6455687430137771,-0.21018989747864358,-0.7754585854425207,0.9472190387813004,-0.5471781968290252,0.4194572984201477,-0.638948617172177,0.7987971538660652,0.14785536851918346,-0.504230644686166,});
		WPC player2 = new WPC(new double[] { -0.1957467301559439,0.9295052116679319,0.5479486515847047,0.9916233571250284,-0.5091827890131313,-0.1659015794357237,-0.6933760315342052,0.41183334025012686,-0.1348178507786053,-0.8270660875078102,-0.5255416684434349,0.5477256648281648,-0.9797329099054157,-0.6550617062819359,-0.03183975653770865,0.3882524354547243,-0.8196580085076937,-0.8397506090492657,0.7999617834001542,-0.1538456141697444,0.5407103822851229,-0.48045028842473947,-0.47773639791024225,0.29441256835454555,-0.9828489432991718,0.19644283357319692,-0.6946991040067485,-0.9614313644990689,-0.8786887561762429,0.15861494984679458,-0.20155764578505098,0.048183734886375174,-0.4091441206136839,0.10366799486018463,0.5958233451314507,0.8927651637859797,0.19041931080171093,-0.8392222117893486,0.6521873797481716,-0.57289112245887,-0.2826665259515173,-0.9815986948615594,0.10182555843622643,0.9900052272105442,0.5913221741638921,-0.9731155232913342,-0.26057425111338706,0.25886936183594766,-0.441876405246326,-0.5760087201415462,0.34252445205407867,-0.028031153466423486,-0.262434093542109,0.07954512595009744,0.8405840056799296,-0.9380269997361221,-0.06998886134070359,0.7273524985055997,0.10600836196522989,-0.48118980041285075,-0.21548660466849778,-0.6488548317526956,0.3177738762733191,0.23441573415263628,});
		//@formatter:on

		InteractionResult result = othello.interact(player1, player2, context.getRandomForThread());
		InteractionResult expected = new InteractionResult(23, 41, 1);
		assertEquals(result, expected, 0.001);

		InteractionResult result2 = othello.interact(player2, player1, context.getRandomForThread());
		InteractionResult expected2 = new InteractionResult(50, 14, 1);
		assertEquals(result2, expected2, 0.001);
	}

	@Test
	public void testMDPRegression1() {
		//@formatter:off
		WPC player1 = new WPC(new double[] { 0.13005979933401246,-0.5949268639190439,-0.5411731074588717,-0.6319496062237047,-0.6219616380453297,-0.23718487514938102,0.1023391301141996,-0.7353940980093898,-0.878285949202136,0.42285439222442367,0.06207788401981462,0.8091541584434383,-0.7720532090879453,-0.7986044710477849,-0.7735294515235709,-0.13087037744295893,0.8691160428648268,0.6727163739604132,0.25429302105239326,-0.459263310068621,-0.19363967712471197,0.9014935033076821,-0.867956033494695,0.6756377253511325,0.5450346520524252,0.7968602353689649,0.7165119714359238,0.27080001764299944,-0.7956618891457681,-0.5488747833356369,0.9785782112449439,0.29595799176258986,-0.13590777231989692,-0.6067172132441248,-0.844725335473901,0.02521555602751402,-0.6931474472266528,-0.13411441590000317,0.9082070486891154,0.13609082827185892,-0.012088185356578851,-0.14604968774169702,0.05413015299152013,0.24511563061123764,0.6394299713486844,-0.170199115343312,-0.05377824514494245,0.34920493167706157,0.2046989017799956,-0.291676926566653,-0.536921892129774,0.7731348146985684,0.6097407968359252,0.7345011535843016,0.6455687430137771,-0.21018989747864358,-0.7754585854425207,0.9472190387813004,-0.5471781968290252,0.4194572984201477,-0.638948617172177,0.7987971538660652,0.14785536851918346,-0.504230644686166,});
		WPC player2 = new WPC(new double[] { -0.1957467301559439,0.9295052116679319,0.5479486515847047,0.9916233571250284,-0.5091827890131313,-0.1659015794357237,-0.6933760315342052,0.41183334025012686,-0.1348178507786053,-0.8270660875078102,-0.5255416684434349,0.5477256648281648,-0.9797329099054157,-0.6550617062819359,-0.03183975653770865,0.3882524354547243,-0.8196580085076937,-0.8397506090492657,0.7999617834001542,-0.1538456141697444,0.5407103822851229,-0.48045028842473947,-0.47773639791024225,0.29441256835454555,-0.9828489432991718,0.19644283357319692,-0.6946991040067485,-0.9614313644990689,-0.8786887561762429,0.15861494984679458,-0.20155764578505098,0.048183734886375174,-0.4091441206136839,0.10366799486018463,0.5958233451314507,0.8927651637859797,0.19041931080171093,-0.8392222117893486,0.6521873797481716,-0.57289112245887,-0.2826665259515173,-0.9815986948615594,0.10182555843622643,0.9900052272105442,0.5913221741638921,-0.9731155232913342,-0.26057425111338706,0.25886936183594766,-0.441876405246326,-0.5760087201415462,0.34252445205407867,-0.028031153466423486,-0.262434093542109,0.07954512595009744,0.8405840056799296,-0.9380269997361221,-0.06998886134070359,0.7273524985055997,0.10600836196522989,-0.48118980041285075,-0.21548660466849778,-0.6488548317526956,0.3177738762733191,0.23441573415263628,});
		//@formatter:on

		InteractionResult expected = new InteractionResult(18.5, 45.5, 61);

		testMDPRegression(player1, player2, expected);
	}

	@Test
	public void testMDPInteractionRegression1() {
		//@formatter:off
		WPC player1 = new WPC(new double[] { 0.13005979933401246,-0.5949268639190439,-0.5411731074588717,-0.6319496062237047,-0.6219616380453297,-0.23718487514938102,0.1023391301141996,-0.7353940980093898,-0.878285949202136,0.42285439222442367,0.06207788401981462,0.8091541584434383,-0.7720532090879453,-0.7986044710477849,-0.7735294515235709,-0.13087037744295893,0.8691160428648268,0.6727163739604132,0.25429302105239326,-0.459263310068621,-0.19363967712471197,0.9014935033076821,-0.867956033494695,0.6756377253511325,0.5450346520524252,0.7968602353689649,0.7165119714359238,0.27080001764299944,-0.7956618891457681,-0.5488747833356369,0.9785782112449439,0.29595799176258986,-0.13590777231989692,-0.6067172132441248,-0.844725335473901,0.02521555602751402,-0.6931474472266528,-0.13411441590000317,0.9082070486891154,0.13609082827185892,-0.012088185356578851,-0.14604968774169702,0.05413015299152013,0.24511563061123764,0.6394299713486844,-0.170199115343312,-0.05377824514494245,0.34920493167706157,0.2046989017799956,-0.291676926566653,-0.536921892129774,0.7731348146985684,0.6097407968359252,0.7345011535843016,0.6455687430137771,-0.21018989747864358,-0.7754585854425207,0.9472190387813004,-0.5471781968290252,0.4194572984201477,-0.638948617172177,0.7987971538660652,0.14785536851918346,-0.504230644686166,});
		WPC player2 = new WPC(new double[] { -0.1957467301559439,0.9295052116679319,0.5479486515847047,0.9916233571250284,-0.5091827890131313,-0.1659015794357237,-0.6933760315342052,0.41183334025012686,-0.1348178507786053,-0.8270660875078102,-0.5255416684434349,0.5477256648281648,-0.9797329099054157,-0.6550617062819359,-0.03183975653770865,0.3882524354547243,-0.8196580085076937,-0.8397506090492657,0.7999617834001542,-0.1538456141697444,0.5407103822851229,-0.48045028842473947,-0.47773639791024225,0.29441256835454555,-0.9828489432991718,0.19644283357319692,-0.6946991040067485,-0.9614313644990689,-0.8786887561762429,0.15861494984679458,-0.20155764578505098,0.048183734886375174,-0.4091441206136839,0.10366799486018463,0.5958233451314507,0.8927651637859797,0.19041931080171093,-0.8392222117893486,0.6521873797481716,-0.57289112245887,-0.2826665259515173,-0.9815986948615594,0.10182555843622643,0.9900052272105442,0.5913221741638921,-0.9731155232913342,-0.26057425111338706,0.25886936183594766,-0.441876405246326,-0.5760087201415462,0.34252445205407867,-0.028031153466423486,-0.262434093542109,0.07954512595009744,0.8405840056799296,-0.9380269997361221,-0.06998886134070359,0.7273524985055997,0.10600836196522989,-0.48118980041285075,-0.21548660466849778,-0.6488548317526956,0.3177738762733191,0.23441573415263628,});
		//@formatter:on

		InteractionResult expected = new InteractionResult(18.5, 45.5, 61);

		InteractionResult result = mdpInteraction.interact(player1, player2, context.getRandomForThread());
		assertEquals(result, expected, 10e-6);
	}

	@Test
	public void testMDPRegression2() {
		//@formatter:off
		WPC player1 = new WPC(new double[] { -0.6602373157611012,0.9607322357854646,0.9242889765661801,-0.5239541106828001,-0.14728375734709465,0.09719520166098272,-0.8304673381261547,-0.12203069947135692,-0.36898423992146867,0.38366123611639713,0.013748441287396496,0.7696012373948808,-0.8345047919230564,-0.21251256157726317,0.7128870732509336,-0.8135439519562029,-0.16913805078456168,-0.6652927585079578,-0.3799785421262638,0.06999741239782553,-0.425974401587633,-0.5448504823148452,-0.6596162124495022,0.1706589728464034,0.9283473708497332,-0.7566216937328374,-0.987884075362031,-0.8963720735470724,-0.996072324318575,-0.3302809106626934,-0.47140150275963855,-0.9201202224484906,-0.9376175162240785,-0.9539599670458832,-0.1936899503890266,-0.6510003856389912,0.022663758612166074,0.16028397188285304,0.6781730002337896,-0.5794812478662976,0.7726964797958358,-0.12297597655198578,-0.7891756056675034,-0.03263117241106084,0.2996178009766428,0.08501328913237849,0.0010998593487738795,0.13676695061830824,-0.11467497781976022,-0.045376663564748565,-0.5327994897759667,-0.6803824799219815,-0.6563202319114518,-0.11598969693819061,-0.5960492158440278,0.39092352135049824,-0.9222914308839862,-0.27900716736402176,0.46766580205661423,-0.8158478543559848,0.09583189028008077,-0.31385903789861525,-0.5529517449486758,0.5119358913478189,});
		WPC player2 = new WPC(new double[] { -0.42876262081845207,0.9182444359426478,0.6002572110763196,0.7474337525063066,-0.38552645299666644,-0.996922429409234,-0.41096782706662127,-0.6240225074402108,0.16399562833724923,-0.29680659635230633,-0.7723498255735963,-0.023396127691629687,0.8324206260466753,0.9321744281117521,-0.3770371714116614,0.7749922572310259,-0.6060164869434583,-0.6163948410417524,-0.8487453081267642,0.9183382589197251,-0.4851465732099216,0.6537376438896305,-0.4463188072361697,0.5751882477741392,0.38929817621579277,-0.9772786157966835,0.6879572349689989,-0.45226434099363644,0.9223336817456844,-0.23971253090607902,0.3635084606997343,-0.35064300112008473,0.22036590341778517,-0.9870500366728665,-0.3535154343931386,0.43243823311065555,-0.5309822739624765,-0.39845466211607894,0.6102638557802185,-0.500605057173777,0.846404105331418,0.7056227462991553,-0.566214438288076,0.12881132530710104,0.9366503953329373,-0.7835095596684414,-0.8540319816281219,-0.7692466874075587,0.1626493401058937,0.7340156391266865,0.2149053678508488,0.677302474861083,0.7138602923031261,-0.9056849499053445,-0.6222284865095675,0.5207008551594781,-0.9545082739220452,-0.3837870434039914,0.2325600326808135,0.6397536334306246,-0.4976315588306188,-0.9412277602004053,-0.9937337712100245,-0.44585803742113317,});
		//@formatter:on

		InteractionResult result = othello2.interact(player1, player2, context.getRandomForThread());
		System.out.println(result);
		InteractionResult mdpResult = mdpInteraction2.interact(player1, player2, context.getRandomForThread());
		System.out.println(mdpResult);
		Assert.assertEquals(mdpResult.firstResult(), result.firstResult(), 0.001);
		Assert.assertEquals(mdpResult.secondResult(), result.secondResult(), 0.001);

		InteractionResult expected = new InteractionResult(38.5, 25.5, 61);
		testMDPRegression(player1, player2, expected);
	}

	@Test
	public void testMDPInteractionRegression2() {
		//@formatter:off
		WPC player1 = new WPC(new double[] { -0.6602373157611012,0.9607322357854646,0.9242889765661801,-0.5239541106828001,-0.14728375734709465,0.09719520166098272,-0.8304673381261547,-0.12203069947135692,-0.36898423992146867,0.38366123611639713,0.013748441287396496,0.7696012373948808,-0.8345047919230564,-0.21251256157726317,0.7128870732509336,-0.8135439519562029,-0.16913805078456168,-0.6652927585079578,-0.3799785421262638,0.06999741239782553,-0.425974401587633,-0.5448504823148452,-0.6596162124495022,0.1706589728464034,0.9283473708497332,-0.7566216937328374,-0.987884075362031,-0.8963720735470724,-0.996072324318575,-0.3302809106626934,-0.47140150275963855,-0.9201202224484906,-0.9376175162240785,-0.9539599670458832,-0.1936899503890266,-0.6510003856389912,0.022663758612166074,0.16028397188285304,0.6781730002337896,-0.5794812478662976,0.7726964797958358,-0.12297597655198578,-0.7891756056675034,-0.03263117241106084,0.2996178009766428,0.08501328913237849,0.0010998593487738795,0.13676695061830824,-0.11467497781976022,-0.045376663564748565,-0.5327994897759667,-0.6803824799219815,-0.6563202319114518,-0.11598969693819061,-0.5960492158440278,0.39092352135049824,-0.9222914308839862,-0.27900716736402176,0.46766580205661423,-0.8158478543559848,0.09583189028008077,-0.31385903789861525,-0.5529517449486758,0.5119358913478189,});
		WPC player2 = new WPC(new double[] { -0.42876262081845207,0.9182444359426478,0.6002572110763196,0.7474337525063066,-0.38552645299666644,-0.996922429409234,-0.41096782706662127,-0.6240225074402108,0.16399562833724923,-0.29680659635230633,-0.7723498255735963,-0.023396127691629687,0.8324206260466753,0.9321744281117521,-0.3770371714116614,0.7749922572310259,-0.6060164869434583,-0.6163948410417524,-0.8487453081267642,0.9183382589197251,-0.4851465732099216,0.6537376438896305,-0.4463188072361697,0.5751882477741392,0.38929817621579277,-0.9772786157966835,0.6879572349689989,-0.45226434099363644,0.9223336817456844,-0.23971253090607902,0.3635084606997343,-0.35064300112008473,0.22036590341778517,-0.9870500366728665,-0.3535154343931386,0.43243823311065555,-0.5309822739624765,-0.39845466211607894,0.6102638557802185,-0.500605057173777,0.846404105331418,0.7056227462991553,-0.566214438288076,0.12881132530710104,0.9366503953329373,-0.7835095596684414,-0.8540319816281219,-0.7692466874075587,0.1626493401058937,0.7340156391266865,0.2149053678508488,0.677302474861083,0.7138602923031261,-0.9056849499053445,-0.6222284865095675,0.5207008551594781,-0.9545082739220452,-0.3837870434039914,0.2325600326808135,0.6397536334306246,-0.4976315588306188,-0.9412277602004053,-0.9937337712100245,-0.44585803742113317,});
		//@formatter:on

		InteractionResult expected = new InteractionResult(38.5, 25.5, 61);

		InteractionResult result = mdpInteraction.interact(player1, player2, context.getRandomForThread());
		assertEquals(result, expected, 10e-6);
	}

	@Test
	public void testMDPRegression3() {
		//@formatter:off
		WPC player1 = new WPC(new double[] { -0.13855581543916085,0.8761995245263621,-0.04785730874939187,0.861019727149499,0.11587324763238582,-0.9770572673635725,0.8820617142868081,-0.3846549589823933,-0.6117825033813169,0.6478810222614615,-0.6361709820368158,-0.7168086424130777,-0.11392582226136305,0.30707695738568974,-0.9098000496620262,-0.1359746417450527,-0.6364182008313803,0.5301625905159515,-0.669180351913703,0.2491266123145408,-0.9939854623909143,-0.41941945415631743,-0.8387901753670364,0.9666577295514833,0.2319121675708571,-0.7802561333983498,0.4640577750374204,-0.6599894073618167,-0.995452111628865,0.741677321764187,-0.5037596208791175,0.6667993704155601,0.38658699494081317,0.9830000318234742,0.8246448649555762,-0.07047060465612942,-0.9123785628840309,-0.5487315884893293,0.6561485203032342,-0.9388845031574031,-0.8748943274883647,-0.3313500316366289,0.41758255610181694,-0.837918907946797,-0.35459568171819944,0.9174593723043931,0.89867192600137,-0.2756600093711232,0.2777390228959189,0.7852677684659879,0.017757790244926408,0.7776208967606411,-0.8833347792627695,0.686386061405837,-0.5843559436727421,0.270679195866967,-0.1365582293806198,0.321648229176247,0.8709313784188559,0.20160812558080243,0.6860920583886805,-0.06953344376944415,0.235337530377981,-0.5582769089516111,});
		WPC player2 = new WPC(new double[] { -0.28710796446922204,0.18365786387063343,-0.5956800132365334,-0.21206004944383627,0.8330845010729897,-0.41760369047413537,0.19845402375210908,0.6483485323270206,-0.07981417605995356,0.18472626594530395,-0.013795472359957817,-0.4011246044587047,0.34406139215762055,-0.7757554283771761,-0.29534739003267774,0.08670901455195246,-0.5956861957733204,-0.32869503499863706,0.7576975540904654,0.7110772569368082,0.1715597163869318,0.6450720371221461,-0.8992698662343517,-0.03884824123863728,0.9706566820717466,0.9910678946519678,0.011921058547515795,0.2104150582460682,0.8119586568622315,0.0815858947707837,0.25531799441879954,-0.2519637808717654,-0.922309804261936,0.6640770820646713,-0.902997384617791,-0.19112132339027266,0.4279451781070396,-0.8600003888414682,-0.7174206446430813,-0.03567081134469596,-0.645094966960646,0.37180882636248125,-0.4175576026348633,0.024274508103987458,-0.8072686700692842,-0.3673089764807931,-0.03277051867321612,0.10767316757672196,0.6453282988377245,0.8876172574853918,-0.05030677537438466,0.8279302638427284,-0.6908785794825412,-0.787206236808879,0.6184246383787602,0.6541126940131496,0.877252968318937,0.785782530533603,-0.7055709792419562,0.4761928301940541,0.9438581853044683,0.7562235017819137,-0.8153070074669793,0.11488420828661638,});
		//@formatter:on

		InteractionResult result = othello2.interact(player1, player2, context.getRandomForThread());
		InteractionResult mdpResult = mdpInteraction2.interact(player1, player2, context.getRandomForThread());
		Assert.assertEquals(mdpResult.firstResult(), result.firstResult(), 0.001);
		Assert.assertEquals(mdpResult.secondResult(), result.secondResult(), 0.001);

		InteractionResult expected = new InteractionResult(13, 51, 61);
		testMDPRegression(player1, player2, expected);
	}

	@Test
	public void testMDPInteractionRegression3() {
		//@formatter:off
		WPC player1 = new WPC(new double[] { -0.13855581543916085,0.8761995245263621,-0.04785730874939187,0.861019727149499,0.11587324763238582,-0.9770572673635725,0.8820617142868081,-0.3846549589823933,-0.6117825033813169,0.6478810222614615,-0.6361709820368158,-0.7168086424130777,-0.11392582226136305,0.30707695738568974,-0.9098000496620262,-0.1359746417450527,-0.6364182008313803,0.5301625905159515,-0.669180351913703,0.2491266123145408,-0.9939854623909143,-0.41941945415631743,-0.8387901753670364,0.9666577295514833,0.2319121675708571,-0.7802561333983498,0.4640577750374204,-0.6599894073618167,-0.995452111628865,0.741677321764187,-0.5037596208791175,0.6667993704155601,0.38658699494081317,0.9830000318234742,0.8246448649555762,-0.07047060465612942,-0.9123785628840309,-0.5487315884893293,0.6561485203032342,-0.9388845031574031,-0.8748943274883647,-0.3313500316366289,0.41758255610181694,-0.837918907946797,-0.35459568171819944,0.9174593723043931,0.89867192600137,-0.2756600093711232,0.2777390228959189,0.7852677684659879,0.017757790244926408,0.7776208967606411,-0.8833347792627695,0.686386061405837,-0.5843559436727421,0.270679195866967,-0.1365582293806198,0.321648229176247,0.8709313784188559,0.20160812558080243,0.6860920583886805,-0.06953344376944415,0.235337530377981,-0.5582769089516111,});
		WPC player2 = new WPC(new double[] { -0.28710796446922204,0.18365786387063343,-0.5956800132365334,-0.21206004944383627,0.8330845010729897,-0.41760369047413537,0.19845402375210908,0.6483485323270206,-0.07981417605995356,0.18472626594530395,-0.013795472359957817,-0.4011246044587047,0.34406139215762055,-0.7757554283771761,-0.29534739003267774,0.08670901455195246,-0.5956861957733204,-0.32869503499863706,0.7576975540904654,0.7110772569368082,0.1715597163869318,0.6450720371221461,-0.8992698662343517,-0.03884824123863728,0.9706566820717466,0.9910678946519678,0.011921058547515795,0.2104150582460682,0.8119586568622315,0.0815858947707837,0.25531799441879954,-0.2519637808717654,-0.922309804261936,0.6640770820646713,-0.902997384617791,-0.19112132339027266,0.4279451781070396,-0.8600003888414682,-0.7174206446430813,-0.03567081134469596,-0.645094966960646,0.37180882636248125,-0.4175576026348633,0.024274508103987458,-0.8072686700692842,-0.3673089764807931,-0.03277051867321612,0.10767316757672196,0.6453282988377245,0.8876172574853918,-0.05030677537438466,0.8279302638427284,-0.6908785794825412,-0.787206236808879,0.6184246383787602,0.6541126940131496,0.877252968318937,0.785782530533603,-0.7055709792419562,0.4761928301940541,0.9438581853044683,0.7562235017819137,-0.8153070074669793,0.11488420828661638,});
		//@formatter:on

		InteractionResult expected = new InteractionResult(13, 51, 61);

		InteractionResult result = mdpInteraction.interact(player1, player2, context.getRandomForThread());
		assertEquals(result, expected, 10e-6);
	}

	@Test
	public void testMDPRegression4() {
		//@formatter:off
		WPC player1 = new WPC(new double[] { 0.83855581543916085,0.4761995245263621,0.04785730874939187,-0.861019727149499,0.11587324763238582,-0.9770572673635725,0.8820617142868081,0.3846549589823933,-0.6117825033813169,0.6478810222614615,-0.6361709820368158,-0.7168086424130777,0.11392582226136305,0.30707695738568974,-0.9098000496620262,-0.1359746417450527,-0.6364182008313803,0.5301625905159515,-0.669180351913703,0.2491266123145408,-0.9939854623909143,-0.41941945415631743,-0.8387901753670364,0.9666577295514833,0.2319121675708571,-0.7802561333983498,0.4640577750374204,-0.6599894073618167,-0.995452111628865,0.741677321764187,-0.5037596208791175,0.6667993704155601,0.38658699494081317,0.9830000318234742,0.8246448649555762,-0.07047060465612942,-0.9123785628840309,-0.5487315884893293,0.6561485203032342,-0.9388845031574031,-0.8748943274883647,-0.3313500316366289,0.41758255610181694,-0.837918907946797,-0.35459568171819944,0.9174593723043931,0.89867192600137,-0.2756600093711232,0.2777390228959189,0.7852677684659879,0.017757790244926408,0.7776208967606411,-0.8833347792627695,0.686386061405837,-0.5843559436727421,0.270679195866967,-0.1365582293806198,0.321648229176247,0.8709313784188559,0.20160812558080243,0.6860920583886805,-0.06953344376944415,0.235337530377981,-0.5582769089516111,});
		WPC player2 = new WPC(new double[] { 0.98710796446922204,-0.19365786387063343,0.5956800132365334,0.21206004944383627,0.8330845010729897,0.41760369047413537,0.19845402375210908,0.6483485323270206,-0.07981417605995356,0.18472626594530395,0.013795472359957817,-0.4011246044587047,0.34406139215762055,-0.7757554283771761,-0.29534739003267774,0.08670901455195246,-0.5956861957733204,-0.32869503499863706,0.7576975540904654,0.7110772569368082,0.1715597163869318,0.6450720371221461,-0.8992698662343517,-0.03884824123863728,0.9706566820717466,0.9910678946519678,0.011921058547515795,0.2104150582460682,0.8119586568622315,0.0815858947707837,0.25531799441879954,-0.2519637808717654,-0.922309804261936,0.6640770820646713,-0.902997384617791,-0.19112132339027266,0.4279451781070396,-0.8600003888414682,-0.7174206446430813,-0.03567081134469596,-0.645094966960646,0.37180882636248125,-0.4175576026348633,0.024274508103987458,-0.8072686700692842,-0.3673089764807931,-0.03277051867321612,0.10767316757672196,0.6453282988377245,0.8876172574853918,-0.05030677537438466,0.8279302638427284,-0.6908785794825412,-0.787206236808879,0.6184246383787602,0.6541126940131496,0.877252968318937,0.785782530533603,-0.7055709792419562,0.4761928301940541,0.9438581853044683,0.7562235017819137,-0.8153070074669793,0.11488420828661638,});
		//@formatter:on

		InteractionResult result = othello2.interact(player1, player2, context.getRandomForThread());
		InteractionResult mdpResult = mdpInteraction2.interact(player1, player2, context.getRandomForThread());
		Assert.assertEquals(mdpResult.firstResult(), result.firstResult(), 0.001);
		Assert.assertEquals(mdpResult.secondResult(), result.secondResult(), 0.001);

		InteractionResult expected = new InteractionResult(21, 43, 61);
		testMDPRegression(player1, player2, expected);
	}

	@Test
	public void testMDPInteractionRegression4() {
		//@formatter:off
		WPC player1 = new WPC(new double[] { 0.83855581543916085,0.4761995245263621,0.04785730874939187,-0.861019727149499,0.11587324763238582,-0.9770572673635725,0.8820617142868081,0.3846549589823933,-0.6117825033813169,0.6478810222614615,-0.6361709820368158,-0.7168086424130777,0.11392582226136305,0.30707695738568974,-0.9098000496620262,-0.1359746417450527,-0.6364182008313803,0.5301625905159515,-0.669180351913703,0.2491266123145408,-0.9939854623909143,-0.41941945415631743,-0.8387901753670364,0.9666577295514833,0.2319121675708571,-0.7802561333983498,0.4640577750374204,-0.6599894073618167,-0.995452111628865,0.741677321764187,-0.5037596208791175,0.6667993704155601,0.38658699494081317,0.9830000318234742,0.8246448649555762,-0.07047060465612942,-0.9123785628840309,-0.5487315884893293,0.6561485203032342,-0.9388845031574031,-0.8748943274883647,-0.3313500316366289,0.41758255610181694,-0.837918907946797,-0.35459568171819944,0.9174593723043931,0.89867192600137,-0.2756600093711232,0.2777390228959189,0.7852677684659879,0.017757790244926408,0.7776208967606411,-0.8833347792627695,0.686386061405837,-0.5843559436727421,0.270679195866967,-0.1365582293806198,0.321648229176247,0.8709313784188559,0.20160812558080243,0.6860920583886805,-0.06953344376944415,0.235337530377981,-0.5582769089516111,});
		WPC player2 = new WPC(new double[] { 0.98710796446922204,-0.19365786387063343,0.5956800132365334,0.21206004944383627,0.8330845010729897,0.41760369047413537,0.19845402375210908,0.6483485323270206,-0.07981417605995356,0.18472626594530395,0.013795472359957817,-0.4011246044587047,0.34406139215762055,-0.7757554283771761,-0.29534739003267774,0.08670901455195246,-0.5956861957733204,-0.32869503499863706,0.7576975540904654,0.7110772569368082,0.1715597163869318,0.6450720371221461,-0.8992698662343517,-0.03884824123863728,0.9706566820717466,0.9910678946519678,0.011921058547515795,0.2104150582460682,0.8119586568622315,0.0815858947707837,0.25531799441879954,-0.2519637808717654,-0.922309804261936,0.6640770820646713,-0.902997384617791,-0.19112132339027266,0.4279451781070396,-0.8600003888414682,-0.7174206446430813,-0.03567081134469596,-0.645094966960646,0.37180882636248125,-0.4175576026348633,0.024274508103987458,-0.8072686700692842,-0.3673089764807931,-0.03277051867321612,0.10767316757672196,0.6453282988377245,0.8876172574853918,-0.05030677537438466,0.8279302638427284,-0.6908785794825412,-0.787206236808879,0.6184246383787602,0.6541126940131496,0.877252968318937,0.785782530533603,-0.7055709792419562,0.4761928301940541,0.9438581853044683,0.7562235017819137,-0.8153070074669793,0.11488420828661638,});
		//@formatter:on

		InteractionResult expected = new InteractionResult(21, 43, 61);

		InteractionResult result = mdpInteraction.interact(player1, player2, context.getRandomForThread());
		assertEquals(result, expected, 10e-6);
	}

	@Test
	public void testMDPRegression5() {
		//@formatter:off
		WPC player1 = new WPC(new double[] { -0.06, -0.14, -0.11, 0.08, 0.14, -0.10, -0.07, 0.15, -0.06, 0.16, -0.18, 0.02, -0.11, -0.19, -0.05, -0.19, 0.13, 0.17, 0.14, 0.06, 0.01, -0.06, -0.16, 0.12, 0.03, 0.00, -0.20, 0.16, 0.11, 0.08, 0.11, 0.20, 0.18, 0.09, -0.12, 0.06, -0.12, -0.02, 0.06, -0.00, -0.14, 0.03, 0.11, -0.13, 0.11, 0.08, 0.15, -0.14, 0.07, 0.16, -0.07, 0.19, 0.08, 0.07, 0.09, -0.15, 0.14, -0.06, -0.15, -0.18, -0.10, 0.05, 0.17, 0.10 });
		WPC player2 = new WPC(new double[] { -3.98, 3.85, -1.97, 6.17, 4.72, 0.84, 3.75, 0.27, 7.70, 5.58, 2.31, 6.47, 2.45, 9.44, -8.43, -5.39, -8.40, 9.57, -0.13, 9.44, 6.75, -0.65, -7.33, -9.42, -0.23, 6.09, 1.83, -7.71, 5.53, -0.79, -7.71, 5.20, 3.64, 1.95, 9.69, 5.55, -2.11, 8.88, -0.37, -4.14, 7.32, 1.73, 0.08, -3.13, 3.42, -6.85, 6.37, 4.78, -6.44, 4.99, 9.17, 3.30, -9.16, -9.29, 1.28, 2.68, 3.14, -6.83, -9.37, 2.48, 9.73, -9.46, -4.16, -1.58 });
		//@formatter:on

		InteractionResult result = othello.interact(player1, player2, context.getRandomForThread());
		System.out.println(result);
		// InteractionResult expected = new InteractionResult(36.5, 27.5, 61);
		// testMDPRegression(player1, player2, expected);
		// InteractionResult result = mdpInteraction.interact(player1, player2, random);
		// System.out.println(result);
//		InteractionResult result = othello2.interact(player1, player2, random);
//		InteractionResult mdpResult = mdpInteraction2.interact(player1, player2, random);
//		Assert.assertEquals(mdpResult.firstResult(), result.firstResult(), 0.001);
//		Assert.assertEquals(mdpResult.secondResult(), result.secondResult(), 0.001);
//
//		InteractionResult expected = new InteractionResult(21, 43, 61);
//		testMDPRegression(player1, player2, expected);
	}

	public void testMDPRegression(WPC player1, WPC player2, InteractionResult expected) {

		OthelloSelfPlayEnvironment othelloEnvironment = new OthelloSelfPlayEnvironment();
		Agent<OthelloState, OthelloMove> agent = RealFunctionGameAgentMapping.getGamePlayingAgent(othelloEnvironment,
			player1);
		Agent<OthelloState, OthelloMove> opponent = RealFunctionGameAgentMapping.getGamePlayingAgent(
			othelloEnvironment, player2);

		Environment<OthelloState, OthelloMove> env1 = new GameOpponentEnvironment<OthelloState, OthelloMove>(
			othelloEnvironment, opponent, Board.WHITE);
		Environment<OthelloState, OthelloMove> env2 = new GameOpponentEnvironment<OthelloState, OthelloMove>(
			othelloEnvironment, opponent, Board.BLACK);

		MDPEpisodeInteraction<OthelloState, OthelloMove> episode = new MDPEpisodeInteraction<>(
			new OthelloPointPerPieceInteractionEvaluator());

		InteractionResult result1 = episode.interact(agent, env1, context.getRandomForThread());
		InteractionResult result2 = episode.interact(agent, env2, context.getRandomForThread());

		assertEquals(InteractionResult.aggregate(result1, result2), expected, 0.001);
	}

	private static void assertEquals(InteractionResult result, InteractionResult expected, double delta) {
		Assert.assertEquals(expected.firstResult(), result.firstResult(), delta);
		Assert.assertEquals(expected.secondResult(), result.secondResult(), delta);
		Assert.assertEquals(expected.getEffort(), result.getEffort());
	}

	private static final int NUM_GAMES = 100;

	@Test
	public void testHeuristicInteractions() throws Exception {
		//@formatter:off
		WPC player1 = new WPC(new double[] { 0.13005979933401246,-0.5949268639190439,-0.5411731074588717,-0.6319496062237047,-0.6219616380453297,-0.23718487514938102,0.1023391301141996,-0.7353940980093898,-0.878285949202136,0.42285439222442367,0.06207788401981462,0.8091541584434383,-0.7720532090879453,-0.7986044710477849,-0.7735294515235709,-0.13087037744295893,0.8691160428648268,0.6727163739604132,0.25429302105239326,-0.459263310068621,-0.19363967712471197,0.9014935033076821,-0.867956033494695,0.6756377253511325,0.5450346520524252,0.7968602353689649,0.7165119714359238,0.27080001764299944,-0.7956618891457681,-0.5488747833356369,0.9785782112449439,0.29595799176258986,-0.13590777231989692,-0.6067172132441248,-0.844725335473901,0.02521555602751402,-0.6931474472266528,-0.13411441590000317,0.9082070486891154,0.13609082827185892,-0.012088185356578851,-0.14604968774169702,0.05413015299152013,0.24511563061123764,0.6394299713486844,-0.170199115343312,-0.05377824514494245,0.34920493167706157,0.2046989017799956,-0.291676926566653,-0.536921892129774,0.7731348146985684,0.6097407968359252,0.7345011535843016,0.6455687430137771,-0.21018989747864358,-0.7754585854425207,0.9472190387813004,-0.5471781968290252,0.4194572984201477,-0.638948617172177,0.7987971538660652,0.14785536851918346,-0.504230644686166,});
		//@formatter:on

		double FORCE_RANDOM_MOVE_PROBABILITY = 0.1;
		PerformanceMeasure<WPC> swhMeasure = new AgainstPlayerPerformanceMeasure<WPC, WPC>(
			new OthelloInteraction<>(new WPCOthelloPlayerMapper(), new WPCOthelloPlayerMapper(), true,
				FORCE_RANDOM_MOVE_PROBABILITY),
			new OthelloStandardWPCHeuristic().create(), NUM_GAMES);

		StatisticalSummary summary = swhMeasure.measure(player1, context).stats();
		Assert.assertEquals(0.1875, summary.getMean(), 0.00001);

		PerformanceMeasure<WPC> meuMeasure = new ExpectedUtility<>(
			new OthelloWPCInteraction(true), new StaticPopulationFactory<WPC>(new UniformRandomPopulationFactory<>(
				new WPCIndividualFactory(OthelloBoard.NUM_FIELDS, -1.0, 1.0)), NUM_GAMES, context.getRandomForThread()), NUM_GAMES);

		StatisticalSummary summary2 = meuMeasure.measure(player1, context).stats();
		Assert.assertEquals(0.505, summary2.getMean(), 0.00001);
	}

	@Test
	public void testHeuristicMDPInteractions() throws Exception {
		//@formatter:off
		WPC player1 = new WPC(new double[] { 0.13005979933401246,-0.5949268639190439,-0.5411731074588717,-0.6319496062237047,-0.6219616380453297,-0.23718487514938102,0.1023391301141996,-0.7353940980093898,-0.878285949202136,0.42285439222442367,0.06207788401981462,0.8091541584434383,-0.7720532090879453,-0.7986044710477849,-0.7735294515235709,-0.13087037744295893,0.8691160428648268,0.6727163739604132,0.25429302105239326,-0.459263310068621,-0.19363967712471197,0.9014935033076821,-0.867956033494695,0.6756377253511325,0.5450346520524252,0.7968602353689649,0.7165119714359238,0.27080001764299944,-0.7956618891457681,-0.5488747833356369,0.9785782112449439,0.29595799176258986,-0.13590777231989692,-0.6067172132441248,-0.844725335473901,0.02521555602751402,-0.6931474472266528,-0.13411441590000317,0.9082070486891154,0.13609082827185892,-0.012088185356578851,-0.14604968774169702,0.05413015299152013,0.24511563061123764,0.6394299713486844,-0.170199115343312,-0.05377824514494245,0.34920493167706157,0.2046989017799956,-0.291676926566653,-0.536921892129774,0.7731348146985684,0.6097407968359252,0.7345011535843016,0.6455687430137771,-0.21018989747864358,-0.7754585854425207,0.9472190387813004,-0.5471781968290252,0.4194572984201477,-0.638948617172177,0.7987971538660652,0.14785536851918346,-0.504230644686166,});
		//@formatter:on

		OthelloSelfPlayEnvironment othelloEnvironment = new OthelloSelfPlayEnvironment();
		Agent<OthelloState, OthelloMove> agent = RealFunctionGameAgentMapping.getGamePlayingAgent(othelloEnvironment,
			player1);
		Agent<OthelloState, OthelloMove> opponent = RealFunctionGameAgentMapping.getGamePlayingAgent(
			othelloEnvironment, new OthelloStandardWPCHeuristic().create());

		RandomizedEnvironment<OthelloState, OthelloMove> randomizedEnvironment = new RandomizedEnvironment<>(
			othelloEnvironment, 0.1, context.getRandomForThread());
		Environment<OthelloState, OthelloMove> env1 = new GameOpponentEnvironment<OthelloState, OthelloMove>(
			randomizedEnvironment, opponent, Board.WHITE);
		Environment<OthelloState, OthelloMove> env2 = new GameOpponentEnvironment<OthelloState, OthelloMove>(
			randomizedEnvironment, opponent, Board.BLACK);

		MDPEpisodeInteraction<OthelloState, OthelloMove> interaction = new MDPEpisodeInteraction<>(
			new OthelloInteractionEvaluator());

		int numGames = NUM_GAMES;
		double totalResult = 0;
		for (int i = 0; i < numGames; i++) {
			InteractionResult result = interaction.interact(agent, env1, context.getRandomForThread());
			totalResult += result.firstResult();
			InteractionResult result2 = interaction.interact(agent, env2, context.getRandomForThread());
			totalResult += result2.firstResult();
		}

		Assert.assertEquals(0.1725, (totalResult / (numGames * 2)), 0.00001);

		WPCIndividualFactory wpcIndividualFactory = new WPCIndividualFactory(OthelloBoard.NUM_FIELDS, -1.0, 1.0);
		UniformRandomPopulationFactory<WPC> uniformFactory = new UniformRandomPopulationFactory<>(wpcIndividualFactory);
		StaticPopulationFactory<WPC> factory = new StaticPopulationFactory<>(uniformFactory, NUM_GAMES, context.getRandomForThread());
		List<WPC> opponents = factory.createPopulation(NUM_GAMES, context.getRandomForThread());

		totalResult = 0;
		for (WPC opp : opponents) {
			opponent = RealFunctionGameAgentMapping.getGamePlayingAgent(othelloEnvironment, opp);
			env1 = new GameOpponentEnvironment<OthelloState, OthelloMove>(othelloEnvironment, opponent, Board.WHITE);
			env2 = new GameOpponentEnvironment<OthelloState, OthelloMove>(othelloEnvironment, opponent, Board.BLACK);
			InteractionResult result = interaction.interact(agent, env1, context.getRandomForThread());
			totalResult += result.firstResult();
			InteractionResult result2 = interaction.interact(agent, env2, context.getRandomForThread());
			totalResult += result2.firstResult();
		}
		Assert.assertEquals(0.4525, totalResult / (numGames * 2), 0.00001);

	}
}