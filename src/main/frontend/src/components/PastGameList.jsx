import { Color } from 'chessgameplay'
import { clientsideDateString } from '../util/clientside_date_converter';
import UserLink from '../components/UserLink';

export default function PastGameList({ games, perspective }) {
    return (
        <ul>
            {games.map(game => {
                const duration = Math.floor((game.ended.getTime() - game.started.getTime()) / 1000);
                
                const winner = game.result === 'WHITE' ? Color.WHITE : Color.BLACK;
                const loser = Color.opposite(winner);
                const pageWon = perspective === game.white && winner === Color.WHITE || perspective === game.black && winner === Color.BLACK;

                const resultText = game.dueTo === 'MATE' ?
                                    `Checkmate. ${Color.toString(winner)} won.`
                                    :
                                    `${Color.toString(loser)} resigned.`;
                
                const resultClass = pageWon ? 'game-victory' : 'game-defeat';
                const localeDate = clientsideDateString(game.started)
                return (
                <li key={game.uuid} className='past-game'>
                    <h3>
                        <UserLink user={game.white}/> vs <UserLink user={game.black}/>
                    </h3>
                    <p>{localeDate}</p>
                    <p>{duration} seconds</p>
                    <p className={resultClass}>{resultText}</p>
                </li>)
            })
            }
        </ul>);
}