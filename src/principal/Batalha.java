/* 
 * This file is part of the LordOfJava distribution (https://github.com/willunicamp/lordofjava).
 * Copyright (c) 2018 William Paiva
 * 
 * This program is free software: you can redistribute it and/or modify  
 * it under the terms of the GNU General Public License as published by  
 * the Free Software Foundation, version 3.
 * 
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License 
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package principal;

import java.io.BufferedReader;
import personagens.Habilidade;
import personagens.Classe;
import personagens.Personagem;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Batalha {
	Equipe aliados;
	Scanner s;
	boolean vezAliados;
	boolean fim;
	Clip clip;
	static int turno = 0;
	
	public Batalha(){
		s = new Scanner(System.in);
		fim = false;
	}
	
	private void playSomEpico(){
	    try {
	        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("epic.wav").getAbsoluteFile());
	        clip = AudioSystem.getClip();
	        clip.open(audioInputStream);
	        clip.start();
	    } catch(Exception ex) {
	        System.out.println("Error with playing sound.");
	        ex.printStackTrace();
	    }
	}
	
	private void paraSomEpico(){
	    try {
	        clip.stop();
	    } catch(Exception ex) {
	        System.out.println("Error with playing sound.");
	        ex.printStackTrace();
	    }
	}
	
	private void espere(int segundos){
		try {
		    Thread.sleep(segundos*1000);                 //1000 milliseconds is one second.
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
	}
	
	public void inicio(){
		Personagem inimigo;
		Equipe equipeInimiga;
		BufferedReader fases;
		Path arquivo;
		String linha, nome;
		Classe.Tipo tipo;
		int nivel;
		print("==========================");
		print("=    O SENHOR DO JAVA    =");
		print("==========================\n");
		
		//ArrayList<Equipe> fases;
		//escolha dos personagens
		escolhePersonagens();
		
		
		//crio as fases (batalhas)
		// = new ArrayList<Equipe>();
		
		//leio o arquivo de fases
		arquivo = Paths.get("game.txt");
		if(Files.exists(arquivo)){
			try {
				fases = Files.newBufferedReader(arquivo);
				equipeInimiga = new Equipe();
				
				while((linha = fases.readLine()) != null){
					String[] comando = linha.split(" ");
					if(comando[0].equals("fase")){
						if(!fim){
							playSomEpico();
							Lutar(equipeInimiga);
							paraSomEpico();
						}
						equipeInimiga = new Equipe();
					}else{
						nome = comando[0];
						tipo = selecionaTipo(comando[1]);
						nivel = Integer.parseInt(comando[2]);
						inimigo = new Personagem(nome, Classe.Tipo.MAGO);
						equipeInimiga.adicionaPersonagem(inimigo);
					}
					
				}
			} catch (IOException ex) {
				Logger.getLogger(Batalha.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
	
	private Classe.Tipo selecionaTipo(String tipo){
		Classe.Tipo retorno = null;
		switch(tipo){
			case "monstro":
				retorno = Classe.Tipo.MONSTRO;
			break;
			case "mago":
				retorno = Classe.Tipo.MAGO;
			break;
			case "arqueiro":
				retorno = Classe.Tipo.ARQUEIRO;
			break;
			case "guerreiro":
				retorno = Classe.Tipo.GUERREIRO;
			break;
		}
		return retorno;
	}
	public void escolhePersonagens(){
		this.aliados = new Equipe();
		Personagem pAux = null;
		int tipo, i;
		String nome;
		
		print("Crie 3 personagens.");
		for(i=0;i<3;i++){
			print("Digite o nome do personagem:");
			nome = s.next();
			do{
				print("Escolha a classe do personagem");
				print("1 - Guerreiro, 2 - Arqueiro, 3 - Mago");
				tipo = s.nextInt();
			}while(tipo <= 0 || tipo > 3);
			switch(tipo){
				case 1:
					pAux = new Personagem(nome, Classe.Tipo.GUERREIRO);
					break;
				case 2:
					pAux = new Personagem(nome, Classe.Tipo.ARQUEIRO);
					break;
				case 3:
					pAux = new Personagem(nome, Classe.Tipo.MAGO);
					break;		
			}
			aliados.adicionaPersonagem(pAux);
		}
		
	}
	

	public void Lutar(Equipe inimigos){
		Personagem atacante, amigo;
		Habilidade habilidade;
		Equipe adversarios;
		Personagem adversario;
		int numAdversario, numAliado;

		int ataque;
		boolean atacou = false;
		print("Iniciando batalha "+ (++Batalha.turno) +" entre Aliados e Inimigos:");
		
		while(aliados.contaConscientes() > 0 && inimigos.contaConscientes() > 0 && !fim){
			atacou = false;
			atacante = null;
			
			atacante = escolheAtacante(inimigos);
			
			if(atacante != null){
				habilidade = null;
				amigo = null;
				adversario = null;
				espere(2);
				print("Aliados: ");
				printPersonagens(aliados);
				print("\nInimigos: ");
				printPersonagens(inimigos);
				
				if(vezAliados){
					adversarios = inimigos;
				}else{
					adversarios = aliados;
				}
				
				do{
					print("--------------------------------------------------------");
					print(atacante.getNome()+", escolha seu ataque:");
					printHabilidades(atacante);
					ataque = s.nextInt();
					habilidade = atacante.getClasse().getHabilidade(ataque);
					
					if(habilidade != null){
						
						if(habilidade.afetaAmigo()){
							do{
								print("Escolha seu aliado que receberá "+habilidade.getNome()+":");
								printPersonagens(aliados);
								numAliado = s.nextInt();
								amigo = aliados.getPersonagem(numAliado);
							}while(amigo == null);
							atacou = atacante.ataca(habilidade, amigo);
						}else{
							if(habilidade.afetaTodos()){
								for(Personagem i: adversarios.getEquipe()){
									atacou = atacante.ataca(habilidade, i);
								}
								print("\nTodos foram afetados por "+habilidade.getNome()+".\n");
							}else{
								do{
									print("Escolha quem recebe o ataque "+habilidade.getNome()+":");
									printPersonagens(adversarios);
									numAdversario = s.nextInt();
									adversario = adversarios.getPersonagem(numAdversario);
								}while(adversario == null);
								
								atacou = atacante.ataca(habilidade, adversario);
							}
						}
					}
					if(atacou == false){
						print("Ataque inválido!");
					}
				}while(atacou == false);
				

				atacou = false;
				print("--------------------------");
				print("Turno finalizado");
				print("--------------------------\n");
				espere(3);
				
				if(inimigos.contaConscientes() == 0){
					print("Vitória dos Aliados");
					aliados.ganharExperiencia(inimigos);
					aliados.reviverTodos();
				}else if(aliados.contaConscientes() == 0){
					print("Vitória dos Inimigos. Fim de jogo...");
					fim = true;
				}	
			}
			aliados.atualizaTemposEspera();
			inimigos.atualizaTemposEspera();
		}
	}
	
	public void print(String p){
		System.out.println(p);
	}
	
	private void printHabilidades(Personagem p){
		print(String.format("%1$-3s %2$-18s %3$10s %4$4s %5$4s","Id","Habilidade","PM","Espera","Dano"));
		for(Habilidade h: p.getClasse().getHabilidades()){
			print(String.format("%1$-3s %2$-18s %3$10s %4$4s %5$4s",h.getID(),h.getNome(),h.getCustoPM(p),h.getTempo(),h.getDano(p)));
		}
	}
	
	private void printPersonagens(Equipe equipe){
		print("--------------------------------------------------------");
		print(String.format("%1$-3s %2$-18s %3$6s %4$4s %5$4s %6$4s %7$7s","Id","Nome","Nível","PV","PM","PE","Espera"));

		for(Personagem p: equipe.getEquipe()){
			print(String.format("%1$-3s %2$-18s %3$6s %4$4s %5$4s %6$4s %7$7s",p.getID(),p.getNome(),p.getNivel(),p.getPV(),p.getPM(),p.getPE(),p.getTempoEspera()));
		}
	}

	private Personagem escolheAtacante(Equipe inimigos){
		Personagem atacante = null;
		Personagem aliado, inimigo;
		int sorte = (int)(Math.random()*100);
		sorte = sorte%2;
		aliado = aliados.proximoQueAtaca();
		inimigo = inimigos.proximoQueAtaca();
		
		if(aliado != null && inimigo != null){
			if(sorte == 0){
				atacante = aliado;
				vezAliados = true;
			}else{
				atacante = inimigo;
				vezAliados = false;
			}
		}else if(inimigo == null){
			atacante = aliado;
			vezAliados = true;
		}else if(aliado == null){
			atacante = inimigo;
			vezAliados = false;
		}
		
		return atacante;
	}

}